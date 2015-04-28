// Adding a comment to the default task.

import star.assistant.Task;
import star.assistant.annotation.StarAssistantTask;
import star.assistant.ui.FunctionTaskController;

@StarAssistantTask(display = "Introduction",
        contentPath = "HTML/Intro.xhtml",
        controller = Task00_Introduction.IntroTaskController.class)
public class Task00_Introduction extends Task {
    
    public class IntroTaskController extends FunctionTaskController {
        
        public void example() {
            notifyUser("Example TaskController function.");
        }
    }
}