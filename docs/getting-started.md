
## Getting Started

```shell
http GET "http://localhost:11435/api/tags"
```

```shell
http GET "http://localhost:11435/api/ps"
```

```shell
http GET "http://localhost:11435/embedding?query=how fix PSQLException query_wait_timeout?"
```

```shell
http GET "http://localhost:11435/embedding?query=what is the slow productivity about?"
```


You can also use the streaming response by setting the stream as true
```shell
http --stream POST "http://localhost:11435/api/generate" model="qwen3:8b" prompt="what is the slow productivity about?" stream:=true 
```

```shell
http POST "http://localhost:11435/api/generate" model="qwen3:8b" prompt="what is the slow productivity about?" stream:=false
```

```shell
http POST "http://localhost:11435/api/chat" model="qwen3:8b" messages:='[{"role": "user", "content": "what is the slow productivity about?"}]' stream:=false
```

You can also use the streaming response by setting the stream as true
```shell
http --stream  POST "http://localhost:11435/api/chat" model="qwen3:8b" messages:='[{"role": "user", "content": "what is the slow productivity about?"}]' stream:=true 
```

# Open Web UI

DATA_DIR=~/.open-webui uvx --python 3.11 open-webui@latest serve