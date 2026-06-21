from fastapi import APIRouter, UploadFile

router = APIRouter()


@router.post("/upload")
async def upload(
        file: UploadFile
):
    content = await file.read()

    with open(
            f"uploads/{file.filename}",
            "wb"
    ) as f:
        f.write(content)

    return {
        "uploaded": file.filename
    }