# 🦙 PKMBridge

A RAG (Retrieval-Augmented Generation) app powered by Ollama and LangChain4j, designed to enrich AI responses with insights from your personal knowledge base — seamlessly integrating with tools like Obsidian or LogseqDB.

This project was built for fun and learning! It is currently in its early stages, and we welcome contributions and feedback from the community to help shape its future.

## Why use PKMBridge?
By integrating your personal knowledge base, PKMBridge provides more accurate and contextually relevant AI responses. This is especially useful for professionals, researchers, and enthusiasts who want to leverage AI with their own curated information.

## How can I use it?

### Getting Started with Docker
TODO

### 🚀 Getting Started as a Developer

To run the application, ensure you have Java 21+ installed.

Then, execute the following command in your terminal:
```
./gradlew api:bootRun
```

This will start the application on `http://localhost:11435`.


### API 

#### Query Embedding
```shell
http GET "http://localhost:11435/embedding?query=how fix PSQLException query_wait_timeout?"
```

#### Generate Text and chat

To interact with your Obsidian vault or Logseq Graph and start generate responses using the specified model and prompt you can use the following commands:

```shell
http POST "http://localhost:11435/api/generate" model="qwen3:8b" prompt="what is the slow productivity about?" stream:=false
```

You can also use the streaming response by setting the stream as true

```shell
http --stream POST "http://localhost:11435/api/generate" model="qwen3:8b" prompt="what is the slow productivity about?" stream:=true 
```


```shell
http POST "http://localhost:11435/api/chat" model="qwen3:8b" messages:='[{"role": "user", "content": "what is the slow productivity about?"}]' stream:=false
```

You can also use the streaming response by setting the stream as true
```shell
http --stream  POST "http://localhost:11435/api/chat" model="qwen3:8b" messages:='[{"role": "user", "content": "what is the slow productivity about?"}]' stream:=true 
```

### Open Web UI

```shell
DATA_DIR=~/.open-webui uvx --python 3.11 open-webui@latest serve
```

# Contribute
Contributions are welcome! Please fork the repository and submit a pull request with your changes.

