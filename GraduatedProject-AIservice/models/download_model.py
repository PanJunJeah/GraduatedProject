# 自动下载翻译模型到本地
from transformers import M2M100ForConditionalGeneration, M2M100Tokenizer

model_name = "facebook/m2m100_418M"
# 下载模型
model = M2M100ForConditionalGeneration.from_pretrained(model_name)
tokenizer = M2M100Tokenizer.from_pretrained(model_name)

# 保存到本地指定文件夹
model.save_pretrained("D:/models/m2m100_418M")
tokenizer.save_pretrained("D:/models/m2m100_418M")
print("模型下载完成！路径：D:/models/m2m100_418M")