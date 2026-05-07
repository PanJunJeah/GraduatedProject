FROM python:3.10-slim

WORKDIR /app

RUN apt-get update && apt-get install -y \
    build-essential \
    && rm -rf /var/lib/apt/lists/*

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY src/ ./src/

ENV MODEL_PATH=/app/models/m2m100_418M
ENV HOST=0.0.0.0
ENV PORT=8000

EXPOSE 8000

CMD ["python", "src/translate_service.py"]
