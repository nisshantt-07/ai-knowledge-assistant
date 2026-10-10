import requests

OLLAMA_URL = "http://localhost:11434/api/generate"
MODEL_NAME = "llama3.2"

def generate_answer(question: str, context_chunks: list):
    context = "\n\n".join(context_chunks)

    prompt = f"""You are a helpful assistant. Use the following context to answer the question.

Context:
{context}

Question: {question}

Instructions: Answer based on the context above. If the context contains relevant information, use it to answer directly.

Answer:"""

    response = requests.post(OLLAMA_URL, json={
        "model": MODEL_NAME,
        "prompt": prompt,
        "stream": False,
        "options": {
            "temperature": 0.2
        }
    })

    result = response.json()
    return result["response"]