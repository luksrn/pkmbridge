# 🦙 PKMBridge

A RAG (Retrieval-Augmented Generation) app powered by Ollama and LangChain4j, designed to enrich AI responses with insights from your personal knowledge base — seamlessly integrating with tools like Obsidian or LogseqDB.

## Features
- **RAG with Ollama**: Leverage the power of Ollama for advanced language model capabilities and utilize LangChain4j for building robust language model applications.
- **Personal Knowledge Base**: Integrate with your personal knowledge base from Obsidian or Logseq.
- **RESTful API**: Interact with the application through a well-defined RESTful API compatible with [OpenWeb Ui](https://github.com/open-webui/open-webui)
- **Streaming Support**: Supports streaming responses for real-time interactions.
- **Multiple Models**: Compatible with various models like Qwen, Llama2, etc.
- **Open Source**: Fully open-source, allowing for community contributions and transparency.

## Why use PKMBridge?
By integrating your personal knowledge base, PKMBridge provides more accurate and contextually relevant AI responses. This is especially useful for professionals, researchers, and enthusiasts who want to leverage AI with their own curated information.

## How can I use it?

### Getting Started with Docker
TODO

### 🚀 Getting Started as a Developer

To run the application, ensure you have Java 21+ installed.

Then, execute the following command in your terminal:
```
./gradlew pkm-bridge-api:bootRun
```

This will start the application on `http://localhost:11435`.


# Contribute
Contributions are welcome! Please fork the repository and submit a pull request with your changes.

# Notice
This project was built for fun and learning, so use it at your own risk! It is currently in its early stages, and we welcome contributions and feedback from the community to help shape its future.
