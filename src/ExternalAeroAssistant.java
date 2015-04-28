import java.util.ArrayList;
import java.util.List;
import star.assistant.SimulationAssistant;
import star.assistant.Task;
import star.assistant.annotation.StarAssistant;


@StarAssistant(display="ExternalAeroAssistant")
public final class ExternalAeroAssistant extends SimulationAssistant {

    public ExternalAeroAssistant() {

        List<Task> tasks = new ArrayList<Task>();
        tasks.add( new Task00_Introduction() );
        tasks.add( new Task01_PreProcessing() );
        tasks.add( new Task02_Solver() );
        tasks.add( new Task03_PostProcessing() );
        setOutline(tasks);
    }
}
