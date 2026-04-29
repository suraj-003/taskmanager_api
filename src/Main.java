import com.sun.net.httpserver.*;
import java.net.InetSocketAddress;

// Main is entry point of application
// Creates server and starts listening for requests
public class Main {

    public static void main(String[] args) throws Exception {

        // Create one shared TaskStore for whole application
        TaskStore store = new TaskStore();

        // Create HTTP server on port 8080
        HttpServer server = HttpServer.create(
            new InetSocketAddress(8080), 0);

        // Register handler for all /tasks routes
        server.createContext("/tasks", new TaskHandler(store));

        // Start server
        server.start();

        System.out.println("Server running on http://localhost:8080/tasks");
        
    }
}