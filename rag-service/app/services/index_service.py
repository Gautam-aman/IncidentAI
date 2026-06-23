from pathlib import Path
from uuid import uuid4

from app.ingestion.pdf_loader import split_pdf, split_text
from app.services.embedding_service import embed
from app.vectorstore.qdrant_store import upsert_chunks


def chunks_from_file(path: str) -> list[str]:
    suffix = Path(path).suffix.lower()
    if suffix == ".pdf":
        return [document.page_content for document in split_pdf(path)]

    with open(path, "r", encoding="utf-8", errors="ignore") as file:
        return split_text(file.read())


def index_file(path: str, source: str) -> int:
    chunks = [
        {
            "id": str(uuid4()),
            "text": text,
            "source": source,
            "embedding": embed(text),
        }
        for text in chunks_from_file(path)
        if text.strip()
    ]

    if not chunks:
        return 0

    return upsert_chunks(chunks)
