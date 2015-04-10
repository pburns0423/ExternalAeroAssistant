import java.util.ArrayList;
import java.util.List;
import star.assistant.SimulationAssistant;
import star.assistant.Task;
import star.assistant.annotation.StarAssistant;


@StarAssistant(display="ExternalAeroAssistant")
public final class NewSimulationAssistant extends SimulationAssistant {

    public NewSimulationAssistant() {

        List<Task> tasks = new ArrayList<Task>();
        tasks.add(new NewTask());
        setOutline(tasks);
    }
}
