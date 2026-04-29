import com.sun.net.httpserver.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collection;


// Routes requests to correct method based on method and path
public class TaskHandler implements HttpHandler {

    private final TaskStore store;

    // Public constructor - Main.java creates TaskHandler with store
    public TaskHandler(TaskStore store) {
        this.store = store;
    }

    // Public - Java HttpHandler interface requires this public
    // Called automatically for every incoming HTTP request
    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        // Route to correct handler based on method and path
        if (method.equals("POST") && path.equals("/tasks")) {
            handleCreate(exchange);

        } else if (method.equals("GET") && path.equals("/tasks")) {
            handleList(exchange);

        } else if (method.equals("PATCH") && path.startsWith("/tasks/")) {
            // String id = path.replace("/tasks/", "");

            String id = path.substring("/tasks/".length());
            handleComplete(exchange, id);

        } else {
            sendResponse(exchange, 404, "{\"error\":\"Not found\"}");
        }
    }

    // Private - only called internally from handle()
    // Handles POST /tasks - creates new task
    private void handleCreate(HttpExchange exchange) throws IOException {
        String body = new String(
            exchange.getRequestBody().readAllBytes());

        String title = extractJson(body, "title");
        String description = extractJson(body, "description");

        // Validate title is present - it is required
        if (title == null || title.isEmpty()) {
            sendResponse(exchange, 400,
                "{\"error\":\"title is required\"}");
            return;
        }

        // Create task object
        Task task = new Task(
            title,
            description == null ? "" : description
        );

        // Get AI priority suggestion
        String[] ai = AIService.getPriority(
            task.getTitle(),
            task.getDescription()
        );

        // Use setters - fields are private now
        task.setPriority(ai[0]);
        task.setPriorityReason(ai[1]);

        // Save to store
        store.create(task);

        // Return created task with 201 status
        sendResponse(exchange, 201, task.toJson());
    }

    // Private - only called internally from handle()
    // Handles GET /tasks - returns all tasks
    private void handleList(HttpExchange exchange) throws IOException {
        Collection<Task> tasks = store.getAll();

        // Build JSON array manually
        StringBuilder sb = new StringBuilder("[");
        for (Task t : tasks) {
            sb.append(t.toJson()).append(",");
        }
        // Remove trailing comma
        if (!tasks.isEmpty()) {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append("]");

        sendResponse(exchange, 200, sb.toString());
    }

    // Private - only called internally from handle()
    // Handles PATCH /tasks/{id} - marks task complete
    private void handleComplete(HttpExchange exchange, String id)
            throws IOException {
        boolean updated = store.markComplete(id);

        if (updated) {
            Task task = store.getById(id);
            sendResponse(exchange, 200, task.toJson());
        } else {
            sendResponse(exchange, 404,
                "{\"error\":\"Task not found\"}");
        }
    }

    // Private helper - sends HTTP response back to caller
    private void sendResponse(HttpExchange ex, int code, String body)
            throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "application/json");
        ex.sendResponseHeaders(code, bytes.length);
        ex.getResponseBody().write(bytes);
        ex.getResponseBody().close(); // must close or response never sends
    }

    // Private helper - extracts field value from JSON string
    // BAD AI OUTPUT FIX #3
    // Original AI code used simple indexOf which breaks on
    // special characters like quotes inside values
    // Fixed with while loop that checks for escaped quotes
    private String extractJson(String json, String key) {
        String search = "\"" + key + "\":\"";
        int start = json.indexOf(search);
        if (start == -1) return null;
        start += search.length();

        //  Walk forward until we find unescaped closing quote
        int end = start;
        while (end < json.length()) {
            if (json.charAt(end) == '"'
                    && json.charAt(end - 1) != '\\') {
                break;
            }
            end++;
        }
        return json.substring(start, end);
    }
}