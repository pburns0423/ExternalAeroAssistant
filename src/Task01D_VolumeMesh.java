// Adding a comment to the default task.

import Tools.PrismLayerCalculator;
import Tools.WallYpCalculator;
import star.assistant.Task;
import star.assistant.annotation.StarAssistantTask;
import star.assistant.ui.FunctionTaskController;
import star.common.Simulation;

@StarAssistantTask(display = "Volume Mesh",
        contentPath = "HTML/VolumeMesh.xhtml",
        controller = Task01D_VolumeMesh.VolumeMeshTaskController.class)
public class Task01D_VolumeMesh extends Task {
        
    public class VolumeMeshTaskController extends FunctionTaskController {
        
        public void launchPrismLayerTool() {
            // grab Simulation
            Simulation sim = getActiveSimulation();

            // launch PrismLayerCalculator Panel
            final PrismLayerCalculator calc = new PrismLayerCalculator(sim);
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    calc.setVisible(true);
                }
            });
        }
        
        public void launchWallYpTool() {
            // grab Simulation
            Simulation sim = getActiveSimulation();

            // launch PrismLayerCalculator Panel
            final WallYpCalculator calc = new WallYpCalculator(sim);
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    calc.setVisible(true);
                }
            });
        }
    }
}
