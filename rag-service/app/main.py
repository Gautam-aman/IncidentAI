from fastapi import FastAPI

app = FastAPI(
    title="IncidentAI RAG Service"
)

@app.get("/")
def health():
    return {
        "status": "UP"
}