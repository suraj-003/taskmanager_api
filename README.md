# Smart Task Manager API

A backend REST API for managing tasks with AI-powered
priority suggestions. Built with Core Java - zero frameworks,
zero external dependencies.

## Tech Stack
- Language: Core Java (JDK 17+)
- HTTP Server: com.sun.net.httpserver (built into JDK)
- Storage: In-memory HashMap
- AI Feature: OpenAI GPT-3.5-turbo API

## Setup

### Prerequisites
- Java JDK 17 or higher
- OpenAI API key

### Set API Key
```bash
# Windows CMD
set OPENAI_API_KEY=your-api-key-here

# Windows PowerShell  
$env:OPENAI_API_KEY="your-api-key-here"
```

### Compile
```bash
javac -d out src/Task.java src/TaskStore.java src/AIService.java src/TaskHandler.java src/Main.java
```

### Run
```bash
java -cp out Main
```

Server starts on http://localhost:8080

## API Endpoints

### Create Task - POST /tasks
```bash
curl -X POST http://localhost:8080/tasks -H "Content-Type: application/json" -d "{\"title\":\"Fix login bug\",\"description\":\"Users cannot login on mobile\"}"
```

Response:
```json
{
  "id": "uuid-here",
  "title": "Fix login bug",
  "description": "Users cannot login on mobile",
  "status": "pending",
  "priority": "high",
  "priorityReason": "Login issues directly impact user access"
}
```

### List All Tasks - GET /tasks
```bash
curl http://localhost:8080/tasks
```

### Mark Complete - PATCH /tasks/{id}
```bash
curl -X PATCH http://localhost:8080/tasks/paste-id-here
```

## AI Feature
When a task is created the API automatically calls OpenAI
to suggest a priority level (low/medium/high) with a reason.
If AI is unavailable the task is still created with
default priority medium - graceful degradation.

## Project Structure
```
taskmanager-api/
├── src/
│   ├── Main.java         - Entry point, starts HTTP server
│   ├── Task.java         - Task data model with getters/setters
│   ├── TaskStore.java    - In-memory storage using LinkedHashMap
│   ├── TaskHandler.java  - HTTP request routing and handling
│   └── AIService.java    - OpenAI API integration
├── out/                  - Compiled class files
├── DECISION_LOG.md       - Technical decisions and AI usage log
└── README.md             - Setup and usage instructions
```