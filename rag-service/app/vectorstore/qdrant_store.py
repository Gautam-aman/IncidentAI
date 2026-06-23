import os

from qdrant_client import QdrantClient
from qdrant_client.http.models import Distance, PointStruct, VectorParams

COLLECTION_NAME = "incidentai_documents"
VECTOR_SIZE = 384

client = QdrantClient(
    host=os.getenv("QDRANT_HOST", "localhost"),
    port=int(os.getenv("QDRANT_PORT", "6333")),
)


def ensure_collection() -> None:
    collections = client.get_collections().collections
    if any(collection.name == COLLECTION_NAME for collection in collections):
        return

    client.create_collection(
        collection_name=COLLECTION_NAME,
        vectors_config=VectorParams(size=VECTOR_SIZE, distance=Distance.COSINE),
    )


def upsert_chunks(chunks: list[dict]) -> int:
    ensure_collection()
    points = [
        PointStruct(
            id=chunk["id"],
            vector=chunk["embedding"],
            payload={
                "text": chunk["text"],
                "source": chunk["source"],
            },
        )
        for chunk in chunks
    ]
    client.upsert(collection_name=COLLECTION_NAME, points=points)
    return len(points)


def search(vector: list[float], limit: int = 5):
    ensure_collection()
    if hasattr(client, "query_points"):
        response = client.query_points(
            collection_name=COLLECTION_NAME,
            query=vector,
            limit=limit,
        )
        return response.points

    return client.search(
        collection_name=COLLECTION_NAME,
        query_vector=vector,
        limit=limit,
    )
