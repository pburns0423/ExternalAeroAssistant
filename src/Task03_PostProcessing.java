// Adding a comment to the default task.

import star.assistant.Task;
import star.assistant.annotation.StarAssistantTask;
import star.assistant.ui.FunctionTaskController;

@StarAssistantTask(display = "Post-Processing",
        contentPath = "HTML/PostProcessing.xhtml",
        controller = Task03_PostProcessing.PostProcessingTaskController.class)
public class Task03_PostProcessing extends Task {
    
    public class PostProcessingTaskController extends FunctionTaskController {
        
        public void example() {
            notifyUser("Example TaskController function.");
        }
    }
}