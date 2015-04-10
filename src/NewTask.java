// Adding a comment to the default task.

import star.assistant.Task;
import star.assistant.annotation.StarAssistantTask;
import star.assistant.ui.FunctionTaskController;

@StarAssistantTask(display = "Default Task",
        contentFragment = "<ul><li><a href=\"staraction:example\">example</a></li></ul>",
        controller = NewTask.NewTaskController.class)
public class NewTask extends Task {
    
    public class NewTaskController extends FunctionTaskController {
        
        public void example() {
            notifyUser("Example TaskController function.");
        }
    }
}