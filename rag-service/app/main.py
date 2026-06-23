from fastapi import FastAPI

from app.api import query, upload

app = FastAPI(
    title="IncidentAI RAG Service"
)

app.include_router(upload.router, prefix="/api", tags=["documents"])
app.include_router(query.router, prefix="/api", tags=["query"])

@app.get("/")
def health():
    return {
        "status": "UP"
    }
