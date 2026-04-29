import java.util.*;

// TaskStore is our in-memory database

public class TaskStore {

   
    // LinkedHashMap preserves insertion order
    // Key = task id, Value = Task object
    private final Map<String, Task> tasks = new LinkedHashMap<>();

    // Public - TaskHandler needs to call this to save new task
    public Task create(Task task) {
        tasks.put(task.getId(), task); // use getter not direct access
        return task;
    }

    // Public - TaskHandler needs this to find one task
    public Task getById(String id) {
        return tasks.get(id);
    }

    // Public - TaskHandler needs this for GET /tasks endpoint
    public Collection<Task> getAll() {
        return tasks.values();
    }

    // Public - TaskHandler calls this for PATCH /tasks/{id}
    public boolean markComplete(String id) {
        Task task = tasks.get(id);
        if (task == null) return false; // task not found
        task.setStatus("completed");    // use setter not direct access
        return true;
    }
}