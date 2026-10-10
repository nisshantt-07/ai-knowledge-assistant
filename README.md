\# AI Knowledge Assistant



A full-stack RAG (Retrieval-Augmented Generation) application. You upload PDF documents and ask questions about them in a chat interface. Answers are generated from the content of your own documents by a local LLM, so nothing is sent to an external AI API.



\## Features



\- Sign up and log in with JWT authentication

\- Upload PDF documents; the text is extracted, chunked and indexed for search

\- Ask questions in a chat UI and get answers grounded in the most relevant parts of your documents

\- Chat history is stored in the database

\- Runs fully on your own machine (Llama 3.2 through Ollama)



\## Architecture



```

React (Vite + Tailwind)

&#x20;       |

&#x20;       v

Spring Boot REST API  <------>  PostgreSQL

(auth, upload, PDF text          (users, documents,

&#x20;extraction)                      chat history)

&#x20;       |  HTTP

&#x20;       v

FastAPI service

(chunking, embeddings, FAISS search, LLM call)

&#x20;       |

&#x20;       v

Ollama (Llama 3.2)

```



\### How a question gets answered



1\. \*\*Upload:\*\* the Spring Boot API receives the PDF and extracts its text with Apache PDFBox.

2\. \*\*Indexing:\*\* the text is sent to the FastAPI service, split into chunks, converted to embeddings with `all-MiniLM-L6-v2` (sentence-transformers) and stored in a FAISS index.

3\. \*\*Retrieval:\*\* when you ask a question, it is embedded the same way and the closest chunks are found with FAISS similarity search.

4\. \*\*Generation:\*\* the question and the retrieved chunks are passed to Llama 3.2 on Ollama, which writes the answer from that context.



\## Tech stack



| Layer | Technology |

| --- | --- |

| Frontend | React, Vite, Tailwind CSS |

| Backend API | Java, Spring Boot, Spring Security, JPA/Hibernate |

| AI service | Python, FastAPI, sentence-transformers, FAISS |

| LLM | Llama 3.2 via Ollama |

| Database | PostgreSQL |

| Auth | JWT, BCrypt |



\## Project structure



```

.

|-- src/                    Spring Boot backend (controllers, services, security config)

|-- pom.xml

|-- RAG-python-service/     FastAPI service (chunking, embeddings, vector store, LLM calls)

`-- rag-frontend/           React frontend (login, signup, upload, chat pages)

```



\## Getting started



\### Prerequisites



\- Java (version as set in `pom.xml`) and Maven (the included `mvnw` works too)

\- Node.js and npm

\- Python 3

\- PostgreSQL

\- \[Ollama](https://ollama.com) with the Llama 3.2 model: `ollama pull llama3.2`



\### 1. Configuration



Create a PostgreSQL database and set the connection details in `src/main/resources/application.properties`. The database password and the JWT signing key are read from environment variables, so no secrets are stored in the repo.



PowerShell:



```powershell

setx DB\_PASSWORD "your\_postgres\_password"

setx JWT\_SECRET "a-random-string-of-at-least-32-characters"

```



Open a new terminal after running `setx` so the variables are picked up.



\### 2. Python AI service



```powershell

cd RAG-python-service

python -m venv venv

.\\venv\\Scripts\\Activate.ps1

pip install -r requirements.txt

uvicorn main:app --reload --port 8000

```



\### 3. Spring Boot backend



```powershell

.\\mvnw spring-boot:run

```



\### 4. Frontend



```powershell

cd rag-frontend

npm install

npm run dev

```



Open the URL that Vite prints (usually http://localhost:5173), create an account, upload a PDF and start asking questions.



\## AI service endpoints



| Endpoint | Purpose |

| --- | --- |

| `/chunk` | Split extracted text into chunks |

| `/store` | Embed chunks and add them to the FAISS index |

| `/search` | Return the chunks most similar to a question |

| `/answer` | Generate an answer from a question and its retrieved context |



\## Notes



\- Uploaded documents, the FAISS index and chunk metadata are created at runtime and are git-ignored.

\- Ollama must be running before you ask a question.

