def chunk_text(text: str, chunk_size: int = 500, overlap: int = 100):
    chunks = []

    if not text:
        return chunks

    if chunk_size <= 0:
        raise ValueError("chunk_size must be greater than 0")

    if overlap < 0 or overlap >= chunk_size:
        raise ValueError("overlap must be >= 0 and less than chunk_size")

    start = 0
    text_length = len(text)

    while start < text_length:
        end = min(start + chunk_size, text_length)

        chunk = text[start:end]
        chunks.append(chunk)

        if end == text_length:
            break

        start = end - overlap

    return chunks