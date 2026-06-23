from fastapi import APIRouter
from pydantic import BaseModel, Field

from app.services.embedding_service import embed
from app.vectorstore.qdrant_store import search

router = APIRouter()


class QueryRequest(BaseModel):
    question: str = Field(min_length=1)
    limit: int = Field(default=5, ge=1, le=20)


@router.post("/ask")
def ask(request: QueryRequest):
    results = search(embed(request.question), request.limit)
    contexts = [
        {
            "score": result.score,
            "source": result.payload.get("source"),
            "text": result.payload.get("text"),
        }
        for result in results
    ]

    return {
        "question": request.question,
        "answer": contexts[0]["text"] if contexts else "No indexed context found.",
        "contexts": contexts,
    }
