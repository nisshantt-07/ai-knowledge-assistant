from fastapi import FastAPI
from pydantic import BaseModel
from typing import List
from chunking import chunk_text
from embeddings import get_embedding
from vector_store import vector_store
from llm_service import generate_answer
from fastapi.middleware.cors import CORSMiddleware

app = FastAPI()
app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:5173"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

class TextRequest(BaseModel):
    text: str
    chunk_size: int = 500
    overlap: int = 100


class EmbedRequest(BaseModel):
    text: str


class StoreRequest(BaseModel):
    text: str
    document_id: int
    chunk_size: int = 500
    overlap: int = 100


class SearchRequest(BaseModel):
    query: str
    top_k: int = 3


class AnswerRequest(BaseModel):
    question: str
    top_k: int = 3


@app.get("/")
def read_root():
    return {"message": "Python AI microservice is running!"}


@app.get("/health")
def health_check():
    return {"status": "healthy"}


@app.post("/chunk")
def create_chunks(request: TextRequest):
    chunks = chunk_text(request.text, request.chunk_size, request.overlap)
    return {
        "total_chunks": len(chunks),
        "chunks": chunks
    }


@app.post("/embed")
def create_embedding(request: EmbedRequest):
    embedding = get_embedding(request.text)
    return {
        "embedding": embedding,
        "dimension": len(embedding)
    }


@app.post("/store")
def store_document(request: StoreRequest):
    # Text ko chunks me todo
    chunks = chunk_text(request.text, request.chunk_size, request.overlap)

    # Har chunk ka embedding banao
    embeddings = [get_embedding(chunk) for chunk in chunks]

    # FAISS me store karo
    vector_store.add_chunks(chunks, embeddings, request.document_id)

    return {
        "message": "Document stored successfully",
        "total_chunks_stored": len(chunks)
    }


@app.post("/search")
def search_chunks(request: SearchRequest):
    # Query ka embedding banao
    query_embedding = get_embedding(request.query)

    # FAISS me sabse similar chunks dhundo
    results = vector_store.search(query_embedding, request.top_k)

    return {
        "query": request.query,
        "results": results
    }


@app.post("/answer")
def answer_question(request: AnswerRequest):
    query_embedding = get_embedding(request.question)
    results = vector_store.search(query_embedding, request.top_k)

    context_chunks = [r["text"] for r in results]

    answer = generate_answer(request.question, context_chunks)

    return {
        "question": request.question,
        "answer": answer,
        "sources": results
    }