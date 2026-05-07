from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import Optional, List, Dict
from transformers import M2M100ForConditionalGeneration, M2M100Tokenizer
import warnings
import os
import torch

warnings.filterwarnings("ignore")

app = FastAPI(title="Translation AI Service", version="1.0.0")

MODEL_PATH = os.environ.get("MODEL_PATH", "/app/models/m2m100_418M")
TORCH_NUM_THREADS = int(os.environ.get("TORCH_NUM_THREADS", "2"))
NUM_BEAMS = int(os.environ.get("NUM_BEAMS", "1"))
MAX_LENGTH = int(os.environ.get("MAX_LENGTH", "256"))

torch.set_num_threads(max(1, TORCH_NUM_THREADS))

model = M2M100ForConditionalGeneration.from_pretrained(
    MODEL_PATH,
    local_files_only=True,
    tie_word_embeddings=False
)

tokenizer = M2M100Tokenizer.from_pretrained(
    MODEL_PATH,
    local_files_only=True
)

device = "cuda" if torch.cuda.is_available() else "cpu"
model = model.to(device)
model.eval()

SUPPORTED_LANGUAGES = {
    "zh": "Chinese",
    "en": "English",
    "ja": "Japanese",
    "ko": "Korean",
    "fr": "French",
    "de": "German",
    "es": "Spanish"
}

LANG_CODES = list(SUPPORTED_LANGUAGES.keys())

class TranslationRequest(BaseModel):
    text: str
    src_lang: str = "zh"
    tgt_lang: str = "en"
    context: Optional[str] = None

class PolishingRequest(BaseModel):
    text: str
    style: str = "formal"
    language: str = "zh"

class BatchTranslationRequest(BaseModel):
    texts: List[str]
    src_lang: str = "zh"
    tgt_lang: str = "en"

@app.get("/")
async def root():
    return {
        "service": "Translation AI Service",
        "version": "1.0.0",
        "status": "running",
        "device": device,
        "supported_languages": SUPPORTED_LANGUAGES
    }

@app.get("/health")
async def health_check():
    return {
        "status": "healthy",
        "device": device,
        "model_loaded": True
    }

@app.get("/api/languages")
async def get_languages():
    return {
        "source_languages": [{"code": code, "name": name} for code, name in SUPPORTED_LANGUAGES.items()],
        "target_languages": [{"code": code, "name": name} for code, name in SUPPORTED_LANGUAGES.items()]
    }

@app.post("/api/translate")
async def translate(request: TranslationRequest):
    try:
        if request.src_lang not in LANG_CODES or request.tgt_lang not in LANG_CODES:
            raise HTTPException(status_code=400, detail="Unsupported language code")

        if not request.text.strip():
            raise HTTPException(status_code=400, detail="Text cannot be empty")

        tokenizer.src_lang = request.src_lang

        context_text = f"{request.context} " if request.context else ""
        full_text = context_text + request.text

        encoded = tokenizer(full_text, return_tensors="pt", padding=True, truncation=True, max_length=512)
        encoded = {k: v.to(device) for k, v in encoded.items()}

        with torch.inference_mode():
            generated_tokens = model.generate(
                **encoded,
                forced_bos_token_id=tokenizer.get_lang_id(request.tgt_lang),
                max_length=MAX_LENGTH,
                num_beams=NUM_BEAMS,
                early_stopping=True
            )

        result = tokenizer.batch_decode(generated_tokens, skip_special_tokens=True)[0]

        return {
            "status": "success",
            "data": result,
            "src_lang": request.src_lang,
            "tgt_lang": request.tgt_lang,
            "device": device
        }

    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Translation failed: {str(e)}")

@app.post("/api/translate/batch")
async def batch_translate(request: BatchTranslationRequest):
    try:
        if request.src_lang not in LANG_CODES or request.tgt_lang not in LANG_CODES:
            raise HTTPException(status_code=400, detail="Unsupported language code")

        tokenizer.src_lang = request.src_lang

        results = []
        for text in request.texts:
            if not text.strip():
                results.append("")
                continue

            encoded = tokenizer(text, return_tensors="pt", padding=True, truncation=True, max_length=512)
            encoded = {k: v.to(device) for k, v in encoded.items()}

            with torch.inference_mode():
                generated_tokens = model.generate(
                    **encoded,
                    forced_bos_token_id=tokenizer.get_lang_id(request.tgt_lang),
                    max_length=MAX_LENGTH,
                    num_beams=NUM_BEAMS,
                    early_stopping=True
                )

            result = tokenizer.batch_decode(generated_tokens, skip_special_tokens=True)[0]
            results.append(result)

        return {
            "status": "success",
            "data": results,
            "count": len(results),
            "src_lang": request.src_lang,
            "tgt_lang": request.tgt_lang
        }

    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Batch translation failed: {str(e)}")

@app.post("/api/polish")
async def polish_text(request: PolishingRequest):
    try:
        if request.style not in ["formal", "casual", "academic", "grammar"]:
            raise HTTPException(status_code=400, detail="Unsupported polishing style")

        style_prompts = {
            "formal": "请将以下文本润色为正式风格：",
            "casual": "请将以下文本润色为口语化风格：",
            "academic": "请将以下文本润色为学术风格：",
            "grammar": "请修正以下文本的语法错误："
        }

        prompt = style_prompts[request.style] + request.text

        tokenizer.src_lang = request.language
        encoded = tokenizer(prompt, return_tensors="pt", padding=True, truncation=True, max_length=512)
        encoded = {k: v.to(device) for k, v in encoded.items()}

        tgt_lang = "zh" if request.language == "en" else "en"
        with torch.inference_mode():
            generated_tokens = model.generate(
                **encoded,
                forced_bos_token_id=tokenizer.get_lang_id(tgt_lang),
                max_length=MAX_LENGTH,
                num_beams=NUM_BEAMS,
                early_stopping=True
            )

        result = tokenizer.batch_decode(generated_tokens, skip_special_tokens=True)[0]

        return {
            "status": "success",
            "data": result,
            "style": request.style,
            "language": request.language
        }

    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Polishing failed: {str(e)}")

if __name__ == "__main__":
    import uvicorn
    host = os.environ.get("HOST", "0.0.0.0")
    port = int(os.environ.get("PORT", "8000"))
    uvicorn.run(app, host=host, port=port)
