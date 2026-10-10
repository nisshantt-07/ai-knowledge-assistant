from chunking import chunk_text

sample_text = "This is a test document. " * 50  # Lamba text banate hain testing ke liye

chunks = chunk_text(sample_text, chunk_size=100, overlap=20)

print(f"Total chunks created: {len(chunks)}")
print("\n--- First chunk ---")
print(chunks[0])
print("\n--- Second chunk ---")
print(chunks[1])