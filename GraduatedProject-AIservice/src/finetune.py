import json
import torch
from transformers import (
    M2M100Tokenizer,
    M2M100ForConditionalGeneration,
    DataCollatorForSeq2Seq,
    TrainingArguments,
    Trainer
)
from peft import LoraConfig, get_peft_model, TaskType
from datasets import Dataset

# ===================== 1. 基础配置 =====================
# 你的本地模型路径（和你服务里的路径一致）
MODEL_PATH = r"D:\pythonprogram\GranduatedProject\models\m2m100_418M"
# 训练后模型保存路径
OUTPUT_DIR = "./lora_finetuned_model"
# 语言设置（中英翻译）
SRC_LANG = "zh"
TGT_LANG = "en"

# ===================== 2. 加载模型 & 分词器 =====================
tokenizer = M2M100Tokenizer.from_pretrained(
    MODEL_PATH,
    local_files_only=True
)
tokenizer.src_lang = SRC_LANG
tokenizer.tgt_lang = TGT_LANG

model = M2M100ForConditionalGeneration.from_pretrained(
    MODEL_PATH,
    local_files_only=True,
    tie_word_embeddings=False
)

# ===================== 3. LoRA配置（核心：轻量微调） =====================
lora_config = LoraConfig(
    task_type=TaskType.SEQ_2_SEQ_LM,  # 翻译任务专用
    r=8,                              # LoRA维度（越小越快）
    lora_alpha=32,
    lora_dropout=0.05,
    target_modules=["q_proj", "v_proj"]  # 微调指定层
)
# 包装模型，开启LoRA微调
model = get_peft_model(model, lora_config)
model.print_trainable_parameters()  # 打印：可训练参数仅0.1%！

# ===================== 4. 加载 & 处理数据 =====================
with open("./data/train_data.json", "r", encoding="utf-8") as f:
    data = json.load(f)

# 转换为模型可读取的格式
def preprocess_function(examples):
    inputs = tokenizer(examples["source"], max_length=128, truncation=True, padding="max_length")
    labels = tokenizer(examples["target"], max_length=128, truncation=True, padding="max_length")
    inputs["labels"] = labels["input_ids"]
    return inputs

dataset = Dataset.from_list(data)
tokenized_dataset = dataset.map(preprocess_function)

# ===================== 5. 训练参数 =====================
training_args = TrainingArguments(
    output_dir=OUTPUT_DIR,
    learning_rate=2e-4,         # 学习率
    per_device_train_batch_size=2,
    num_train_epochs=5,         # 训练轮数（5轮足够）
    logging_steps=1,
    save_strategy="epoch",
    fp16=False,                 # CPU必须关闭FP16
    optim="adamw_torch",
    report_to="none"
)

# 数据对齐器
data_collator = DataCollatorForSeq2Seq(tokenizer, model=model)

# ===================== 6. 开始训练 =====================
trainer = Trainer(
    model=model,
    args=training_args,
    train_dataset=tokenized_dataset,
    data_collator=data_collator
)

trainer.train()

# 保存微调后的LoRA模型
model.save_pretrained(OUTPUT_DIR)
tokenizer.save_pretrained(OUTPUT_DIR)

print("✅ 微调完成！模型保存在：", OUTPUT_DIR)