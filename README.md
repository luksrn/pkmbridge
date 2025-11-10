# 🦙 PKMBridge

A RAG (Retrieval-Augmented Generation) service designed to enrich AI responses with insights from your personal knowledge base — seamlessly integrating with tools like Obsidian or LogseqDB.

This project was built for fun and learning, and it is currently in its early stages! You are welcome to contribute.

## How can I use it?

### 🚀 Getting Started as a Developer

### Prerequisites

#### 1. Run a local LLM server
- Ollama installed and running locally. You can download it from [Ollama's official website](https://ollama.com/).

```
ollama run qwen3:8b
```

You can customize the model as you wish. Just make sure it is running locally. 

The default values are:

```shell
export OLLAMA_MODEL_NAME=qwen3:8b
export OLLAMA_BASE_URL=http://localhost:11434
```

#### 2. Configure your PKM access

##### 2.1 LogseqDB

```
export PKM_LOGSEQ_SERVER_URL=http://127.0.0.1:12315/api
export PKM_LOGSEQ_AUTHORIZATION_TOKEN=0432483b-469a-4ca6-b16d-35106294b36e
```


##### 2.2 Obsidian
```
export PKM_OBSIDIAN_FILE_SYSTEM_PATH=/Path/To/obsidian
```

#### 3. Download and Re Ranker Model

Recommendation to download the model and place it in a known location. 

You can find more information at: https://huggingface.co/cross-encoder/ms-marco-MiniLM-L6-v2

```

cd ~
git clone https://huggingface.co/cross-encoder/ms-marco-MiniLM-L6-v2

export RERANK_ENABLED=true
export RERANK_PATH_TO_MODEL=~/ms-marco-MiniLM-L6-v2/onnx/model.onnx
export RERANK_PATH_TO_TOKENIZER=~/ms-marco-MiniLM-L6-v2/tokenizer.json
```

#### 5 Run the server

Then, execute the following command in your terminal:

```

./gradlew api:bootRun
```

This will start the application on `http://localhost:11435`.


### API 

#### Generate Endpoint

```shell
http POST "http://localhost:11435/api/generate" model="qwen3:8b" prompt="How can I evaluate AI responses?" stream:=false

HTTP/1.1 200 
Connection: keep-alive
Content-Type: application/x-ndjson
Date: Sat, 08 Nov 2025 22:59:54 GMT
Keep-Alive: timeout=60
Transfer-Encoding: chunked
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers

{
    "created_at": "2025-11-08T22:59:54.066164Z",
    "done": true,
    "done_reason": "STOP",
    "eval_count": 1962,
    "eval_duration": 78153942333,
    "load_duration": 0,
    "message": {
        "content": "",
        "role": "assistant"
    },
    "model": "qwen3:8b",
    "prompt_eval_count": 1052,
    "prompt_eval_duration": 0,
    "response": "To evaluate AI responses ....",
    "total_duration": 78153942333
}
```

You can also use the Stream Mode:


```shell
http POST "http://localhost:11435/api/generate" model="qwen3:8b" prompt="How can I evaluate AI responses?" stream:=true 
HTTP/1.1 200 
Connection: keep-alive
Content-Type: application/x-ndjson
Date: Sun, 09 Nov 2025 11:49:14 GMT
Keep-Alive: timeout=60
Transfer-Encoding: chunked
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers

{"model":"qwen3:8b","created_at":"2025-11-09T11:49:14.675414Z","message":{"role":"assistant","content":"To"},"done":false}
{"model":"qwen3:8b","created_at":"2025-11-09T11:49:14.715418Z","message":{"role":"assistant","content":" evaluate"},"done":false}
{"model":"qwen3:8b","created_at":"2025-11-09T11:49:14.758676Z","message":{"role":"assistant","content":" AI"},"done":false}
{"model":"qwen3:8b","created_at":"2025-11-09T11:49:14.804370Z","message":{"role":"assistant","content":" responses"},"done":false}
{"model":"qwen3:8b","created_at":"2025-11-09T11:49:14.847418Z","message":{"role":"assistant","content":" effectively"},"done":false}
{"model":"qwen3:8b","created_at":"2025-11-09T11:49:14.890372Z","message":{"role":"assistant","content":","},"done":false}
{"model":"qwen3:8b","created_at":"2025-11-09T11:49:14.933820Z","message":{"role":"assistant","content":" consider"},"done":false}
{"model":"qwen3:8b","created_at":"2025-11-09T11:49:14.976259Z","message":{"role":"assistant","content":" the"},"done":false}
{"model":"qwen3:8b","created_at":"2025-11-09T11:49:15.018584Z","message":{"role":"assistant","content":" following"},"done":false}
{"model":"qwen3:8b","created_at":"2025-11-09T11:49:15.061195Z","message":{"role":"assistant","content":" methods"},"done":false}
{"model":"qwen3:8b","created_at":"2025-11-09T11:49:15.103442Z","message":{"role":"assistant","content":" and"},"done":false}
{"model":"qwen3:8b","created_at":"2025-11-09T11:49:15.145865Z","message":{"role":"assistant","content":" tools"},"done":false}
{"model":"qwen3:8b","created_at":"2025-11-09T11:49:15.189178Z","message":{"role":"assistant","content":" based"},"done":false}
...
{"model":"qwen3:8b","created_at":"2025-11-09T11:49:40.561800Z","message":{"role":"assistant","content":""},"done":true,"done_reason":"STOP","total_duration":71571574333,"load_duration":0,"prompt_eval_count":1052,"prompt_eval_duration":0,"eval_count":1899,"eval_duration":71571574333}
```

### Open Web UI

You can run the Open Web UI version of this project using `uvx`:

```shell
DATA_DIR=~/.open-webui uvx --python 3.11 open-webui@latest serve
```

Be aware that you must change the settings to use the PKMBridge URL.

# Contribute
Contributions are welcome! Please fork the repository and submit a pull request with your changes.

