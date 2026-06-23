from pathlib import Path

from fastapi import APIRouter, File, HTTPException, UploadFile

from app.services.index_service import index_file

router = APIRouter()
UPLOAD_DIR = Path("uploads")


@router.post("/upload")
async def upload(
        file: UploadFile = File(...)
):
    if not file.filename:
        raise HTTPException(status_code=400, detail="Uploaded file must have a filename.")

    UPLOAD_DIR.mkdir(parents=True, exist_ok=True)
    destination = UPLOAD_DIR / Path(file.filename).name
    content = await file.read()

    with open(destination, "wb") as f:
        f.write(content)

    indexed_chunks = index_file(str(destination), destination.name)

    return {
        "uploaded": destination.name,
        "indexed_chunks": indexed_chunks
    }
