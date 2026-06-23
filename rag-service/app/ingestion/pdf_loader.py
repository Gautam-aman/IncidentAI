from langchain_community.document_loaders import PyPDFLoader
from langchain.text_splitter import RecursiveCharacterTextSplitter


splitter = RecursiveCharacterTextSplitter(
    chunk_size=700,
    chunk_overlap=120
)


def load_pdf(path: str):
    loader = PyPDFLoader(path)
    return loader.load()


def load_text(path: str):
    with open(path, "r", encoding="utf-8", errors="ignore") as file:
        return file.read()


def split_pdf(path: str):
    return splitter.split_documents(load_pdf(path))


def split_text(text: str) -> list[str]:
    return splitter.split_text(text)
