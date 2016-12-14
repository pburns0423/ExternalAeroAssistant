// Adding a comment to the default task.

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import star.assistant.Task;
import star.assistant.annotation.StarAssistantTask;
import star.assistant.ui.FunctionTaskController;
import star.common.*;
import star.energy.*;
import star.flow.*;
import star.material.*;

@StarAssistantTask(display = "Pre-Processing",
        contentPath = "HTML/PreProcessing.xhtml",
        controller = Task01_PreProcessing.PreProcessingTaskController.class)
public class Task01_PreProcessing extends Task {
    
    public Task01_PreProcessing() {
    
        List<Task> subTasks = new ArrayList<>();
        subTasks.add(new Task01A_CAD());
        subTasks.add(new Task01D_VolumeMesh());
        setSubtasks(subTasks);
    }
    
    public class PreProcessingTaskController extends FunctionTaskController {

        public void createAllAeroParameters() {
            Collection<String> paramNames = 
                    new ArrayList<>( Arrays.asList("Re","Ma","Pr","gamma", "Tref", "Lref") );
            double[] defaultVals = new double[] {1.0e6, 0.7, 0.72, 1.4, 300.0, 1.0};
            
            int i=0;
            for (String param : paramNames) {
                setOrCreateParameter(param, defaultVals[i++]);
            }
            
            // show node
            Simulation sim = getActiveSimulation();
            selectAndExpandNode(sim.get(GlobalParameterManager.class));
        }
        
        public void setMaterialPropertiesFromParameters() {
            // get physics continuum (exit if more than 1)
            Simulation sim = getActiveSimulation();
            Collection<PhysicsContinuum> physicsContinuum_all = sim.getContinuumManager().getObjectsOf(PhysicsContinuum.class);
            
            PhysicsContinuum physicsContinuum_0;
            if (physicsContinuum_all.isEmpty()) {
                sim.println("  *** No physics continua present... no action taken ***  ");
            } else if (physicsContinuum_all.size() > 1) {
                sim.println("  *** More than 1 physics continua present... no action taken ***  ");
            } else if (physicsContinuum_all.size() == 1) {
                physicsContinuum_0 = physicsContinuum_all.iterator().next();
                if (checkForGasModel(physicsContinuum_0)) {
                    SingleComponentGasModel singleComponentGasModel_0 = physicsContinuum_0.getModelManager().getModel(SingleComponentGasModel.class);
                    Gas gas_0 = ((Gas) singleComponentGasModel_0.getMaterial());

                    // get non-dimensional numbers
                    double Re = getParameterValue( "Re" );
                    double Ma = getParameterValue( "Ma" );
                    double Pr = getParameterValue( "Pr" );
                    double gamma = getParameterValue( "gamma" );
                    
                    // get reference values
                    double Pref = physicsContinuum_0.getReferenceValues().get(ReferencePressure.class).getInternalValue();
                    double Tref = getParameterValue( "Tref" );
                    double Lref = getParameterValue( "Lref" );
                    double molarMass = ((ConstantMaterialPropertyMethod) gas_0.getMaterialProperties().getMaterialProperty(MolecularWeightProperty.class).getMethod()).getQuantity().getInternalValue();
                    double Rgas = 8.3144598/(molarMass/1000.0);
                    sim.println("  *** Using Rgas = " + Rgas + " assuming molar mass in units of kg/kmol ***  ");
                    
                    // set material property values
                    double uVal = Ma*Math.sqrt( gamma*Rgas*Tref );
                    double rhoVal = Pref/(Rgas*Tref);
                    
                    double muVal = rhoVal*uVal*Lref/Re;
                    ConstantMaterialPropertyMethod muProperty = (ConstantMaterialPropertyMethod) gas_0.getMaterialProperties().getMaterialProperty(DynamicViscosityProperty.class).getMethod();
                    muProperty.getQuantity().setValue(muVal);

                    // specific heat
                    double cpVal = (gamma)/(gamma-1.0)*Rgas;
                    ConstantSpecificHeat cpProperty = ((ConstantSpecificHeat) gas_0.getMaterialProperties().getMaterialProperty(SpecificHeatProperty.class).getMethod());
                    cpProperty.getQuantity().setValue(cpVal);

                    // thermal conductivity
                    double kappaVal = muVal*cpVal/Pr;
                    ConstantMaterialPropertyMethod kappaProperty = ((ConstantMaterialPropertyMethod) gas_0.getMaterialProperties().getMaterialProperty(ThermalConductivityProperty.class).getMethod());
                    kappaProperty.getQuantity().setValue(kappaVal);
                    
                    // create reference parameters for uVal and rhoVal
                    setOrCreateParameter( "U_ref", uVal );
                    setOrCreateParameter( "rho_ref", rhoVal );
                    
                    // expand node
                    selectAndExpandNode(singleComponentGasModel_0.getMaterial().getMaterialProperties());
                }
            }
            
        }

        // ===============================================
        // helper routines
        // ===============================================
        public void setOrCreateParameter( String parameterName, double parameterValue ) {
            // get sim
            Simulation sim = getActiveSimulation();
            
            // get parameter is exists or create if it doesn't
            ScalarGlobalParameter scalarGlobalParameter_0;
            try {
                scalarGlobalParameter_0 = (ScalarGlobalParameter) sim.get(GlobalParameterManager.class).getObject(parameterName);
                sim.println("  *** Parameter " + parameterName + " already exists. Old value = " + scalarGlobalParameter_0.getQuantity().getInternalValue() + " ***  " );
            } catch (Exception ex) {
                sim.get(GlobalParameterManager.class).createGlobalParameter(ScalarGlobalParameter.class, "Scalar");
                scalarGlobalParameter_0 = ((ScalarGlobalParameter) sim.get(GlobalParameterManager.class).getObject("Scalar"));
                scalarGlobalParameter_0.setPresentationName(parameterName);
            }
            
            // set value
            scalarGlobalParameter_0.getQuantity().setValue(parameterValue);
            
        }
        
        public double getParameterValue( String parameterName ) {
            Simulation sim = getActiveSimulation();
            double val = 1.0;
            
            // get parameter is exists or create if it doesn't
            ScalarGlobalParameter scalarGlobalParameter_0;
            try {
                scalarGlobalParameter_0 = (ScalarGlobalParameter) sim.get(GlobalParameterManager.class).getObject(parameterName);
                val = scalarGlobalParameter_0.getQuantity().getInternalValue();
            } catch (Exception ex) {
                sim.println("  *** Could not find parameter... using value of 1.0 ***  ");
            }
            
            return val;
        }
        
        public boolean checkForGasModel( PhysicsContinuum phys0 ) {
            boolean hasModel = false;
            for (Model model_0 : phys0.getModelManager().getObjects()) {
                if (model_0 instanceof IdealGasModel) {
                    hasModel = true;
                    break;
                }
            }
            return hasModel;
        }
    }
    
   
}