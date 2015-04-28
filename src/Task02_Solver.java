// Adding a comment to the default task.

import star.assistant.Task;
import star.assistant.annotation.StarAssistantTask;
import star.assistant.ui.FunctionTaskController;

@StarAssistantTask(display = "Solver Settings",
        contentPath = "HTML/Solver.xhtml",
        controller = Task02_Solver.SolverTaskController.class)
public class Task02_Solver extends Task {
    
    public class SolverTaskController extends FunctionTaskController {
        
        public void example() {
            notifyUser("Example TaskController function.");
        }
    }
}