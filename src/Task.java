import java.util.UUID;


// All fields private - no direct access allowed from outside
public class Task {

    // Private fields - only accessible through getters/setters
    private String id;
    private String title;
    private String description;
    private String status;
    private String priority;
    private String priorityReason;

    // Public constructor - 
    public Task(String title, String description) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.description = description;
        this.status = "pending";   // every new task starts pending
        this.priority = "medium";  // default before AI responds
        this.priorityReason = "";  // empty before AI responds
    }

    // GETTERS - public so other classes can READ values
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public String getPriority() {
        return priority;
    }

    public String getPriorityReason() {
        return priorityReason;
    }

    // SETTERS - only for fields that are allowed to change
    // No setId()    - id never changes after creation
    // No setTitle() - title never changes after creation
    // No setDescription() - description never changes

    // Status can change - pending to completed
    public void setStatus(String status) {
        this.status = status;
    }

    // Priority set by AI after creation
    public void setPriority(String priority) {
        this.priority = priority;
    }

    // Priority reason set by AI after creation
    public void setPriorityReason(String priorityReason) {
        this.priorityReason = priorityReason;
    }

    // Converts Task object to JSON string
    // Called when sending response back to API caller
    public String toJson() {
        return String.format(
            "{\"id\":\"%s\",\"title\":\"%s\",\"description\":\"%s\"," +
            "\"status\":\"%s\",\"priority\":\"%s\",\"priorityReason\":\"%s\"}",
            getId(),
            getTitle(),
            getDescription(),
            getStatus(),
            getPriority(),
            getPriorityReason()
        );
    }
}