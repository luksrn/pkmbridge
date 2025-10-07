
```shell
http GET "http://localhost:11435/embedding?query=how fix PSQLException query_wait_timeout?"
```

```shell
http GET "http://localhost:11435/embedding?query=what is the slow productivity about?"
```


```shell
curl -v -i -H 'Content-Type: application/json'  http://localhost:11435/api/chat -d '{"model": "qwen3:8b","messages": [{  "role": "user",  "content": "what is the slow productivity about?"}], "stream": false}'
```

You can also use the streaming response by setting the stream as true
```shell
curl -v -i -H 'Content-Type: application/json'  http://localhost:11435/api/chat -d '{"model": "qwen3:8b","messages": [{  "role": "user",  "content": "what is the slow productivity about?"}], "stream": true}' 
```