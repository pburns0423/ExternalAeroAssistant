// Adding a comment to the default task.

import java.util.ArrayList;
import java.util.List;
import star.assistant.Task;
import star.assistant.annotation.StarAssistantTask;
import star.assistant.ui.FunctionTaskController;

@StarAssistantTask(display = "Pre-Processing",
        contentPath = "HTML/PreProcessing.xhtml",
        controller = Task01_PreProcessing.PreProcessingTaskController.class)
public class Task01_PreProcessing extends Task {
    
    public Task01_PreProcessing() {
    
        List<Task> subTasks = new ArrayList<Task>();
        subTasks.add(new Task01D_VolumeMesh());
        setSubtasks(subTasks);
    }
    
    public class PreProcessingTaskController extends FunctionTaskController {
        
        public void example() {
            notifyUser("Example TaskController function.");
        }
    }
}