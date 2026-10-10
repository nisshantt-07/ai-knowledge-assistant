from embeddings import get_embedding
import numpy as np

def cosine_similarity(vec1, vec2):
    vec1 = np.array(vec1)
    vec2 = np.array(vec2)
    dot_product = np.dot(vec1, vec2)
    norm1 = np.linalg.norm(vec1)
    norm2 = np.linalg.norm(vec2)
    return dot_product / (norm1 * norm2)

text1 = "How do I apply for leave?"
text2 = "What is the process to request time off?"
text3 = "What is the weather today?"

emb1 = get_embedding(text1)
emb2 = get_embedding(text2)
emb3 = get_embedding(text3)

similarity_1_2 = cosine_similarity(emb1, emb2)
similarity_1_3 = cosine_similarity(emb1, emb3)

print(f"Similarity between '{text1}' and '{text2}': {similarity_1_2:.4f}")
print(f"Similarity between '{text1}' and '{text3}': {similarity_1_3:.4f}")