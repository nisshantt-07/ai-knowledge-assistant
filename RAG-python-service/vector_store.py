import faiss
import numpy as np
import pickle
import os

INDEX_FILE = "faiss_index.bin"
METADATA_FILE = "chunks_metadata.pkl"

class VectorStore:
    def __init__(self, dimension=384):
        self.dimension = dimension
        self.index = faiss.IndexFlatL2(dimension)
        self.chunks_metadata = []  # yahan hum store karenge kaunsa chunk kaunse document ka hai

        # Agar pehle se saved index hai, use load karo
        if os.path.exists(INDEX_FILE):
            self.index = faiss.read_index(INDEX_FILE)
            with open(METADATA_FILE, "rb") as f:
                self.chunks_metadata = pickle.load(f)

    def add_chunks(self, chunks, embeddings, document_id):
        embeddings_array = np.array(embeddings).astype('float32')
        self.index.add(embeddings_array)

        for chunk in chunks:
            self.chunks_metadata.append({
                "text": chunk,
                "document_id": document_id
            })

        self._save()

    def search(self, query_embedding, top_k=3):
        query_array = np.array([query_embedding]).astype('float32')
        distances, indices = self.index.search(query_array, top_k)

        results = []
        for idx in indices[0]:
            if idx < len(self.chunks_metadata):
                results.append(self.chunks_metadata[idx])

        return results

    def _save(self):
        faiss.write_index(self.index, INDEX_FILE)
        with open(METADATA_FILE, "wb") as f:
            pickle.dump(self.chunks_metadata, f)


# Global instance — poore app me yahi ek instance use hoga
vector_store = VectorStore()