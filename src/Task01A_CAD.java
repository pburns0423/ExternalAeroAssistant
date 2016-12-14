// Adding a comment to the default task.

import Tools.NacaPanel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import star.assistant.Task;
import star.assistant.annotation.StarAssistantTask;
import star.assistant.ui.FunctionTaskController;
import star.base.neo.*;
import star.cadmodeler.*;
import star.common.*;

@StarAssistantTask(display = "3D-CAD tools",
        contentPath = "HTML/CAD.xhtml",
        controller = Task01A_CAD.CADTaskController.class)
public class Task01A_CAD extends Task {
        
    public class CADTaskController extends FunctionTaskController {

        public void buildRectangularDomain() {

            // preliminaries
            Simulation sim = getActiveSimulation();
            CadModel cadModel_0 = ((CadModel) sim.get(SolidModelManager.class).getObject("3D-CAD Model 1"));
            CanonicalSketchPlane canonicalSketchPlane_0 = ((CanonicalSketchPlane) cadModel_0.getFeature("XY"));

            // build sketch
            Sketch sketch_0 = cadModel_0.getFeatureManager().createSketch(canonicalSketchPlane_0);
            sketch_0.setPresentationName("Sketch: Rectangular Farfield");
            cadModel_0.getFeatureManager().startSketchEdit(sketch_0);
            Units units_m = sim.getUnitsManager().getUnits("m");

            // create points for sketch
            PointSketchPrimitive point_0 = sketch_0.createPoint(new DoubleVector(new double[]{ 0.0, 0.0}));
            PointSketchPrimitive point_1 = sketch_0.createPoint(new DoubleVector(new double[]{-1.0, 0.0}));
            PointSketchPrimitive point_2 = sketch_0.createPoint(new DoubleVector(new double[]{-1.0, 1.0}));
            PointSketchPrimitive point_3 = sketch_0.createPoint(new DoubleVector(new double[]{ 1.0, 1.0}));
            PointSketchPrimitive point_4 = sketch_0.createPoint(new DoubleVector(new double[]{ 1.0,-1.0}));
            PointSketchPrimitive point_5 = sketch_0.createPoint(new DoubleVector(new double[]{-1.0,-1.0}));
            
            // make lines
            LineSketchPrimitive lineSketchPrimitive_0 = sketch_0.createLine(point_0, point_1);
            sketch_0.createHorizontalConstraint(lineSketchPrimitive_0);
            LineSketchPrimitive lineSketchPrimitive_1 = sketch_0.createLine(point_1, point_2);
            sketch_0.createVerticalConstraint(lineSketchPrimitive_1);
            LineSketchPrimitive lineSketchPrimitive_2 = sketch_0.createLine(point_2, point_3);
            sketch_0.createHorizontalConstraint(lineSketchPrimitive_2);
            LineSketchPrimitive lineSketchPrimitive_3 = sketch_0.createLine(point_3, point_4);
            sketch_0.createVerticalConstraint(lineSketchPrimitive_3);
            LineSketchPrimitive lineSketchPrimitive_4 = sketch_0.createLine(point_4, point_5);
            sketch_0.createHorizontalConstraint(lineSketchPrimitive_4);
            LineSketchPrimitive lineSketchPrimitive_5 = sketch_0.createLine(point_5, point_1);
            sketch_0.createVerticalConstraint(lineSketchPrimitive_5);

            // add constraints
            sketch_0.createFixationConstraint(point_0);
            sketch_0.setConstructionState(new NeoObjectVector(new Object[]{lineSketchPrimitive_0}), true);
            sketch_0.createEqualLengthConstraint(lineSketchPrimitive_1, lineSketchPrimitive_5);

            // create dimensions
            LengthDimension lengthDimension_0 = sketch_0.createLengthDimension(lineSketchPrimitive_0, 1.0, units_m);
            lengthDimension_0.getLength().createDesignParameter("L_up");

            HorizontalDistanceDimension horizontalDistanceDimension_0 = sketch_0.createHorizontalDistanceDimension(point_0, point_3, 1.0, units_m);
            horizontalDistanceDimension_0.getDistance().createDesignParameter("L_down");

            LengthDimension lengthDimension_1 = sketch_0.createLengthDimension(lineSketchPrimitive_1, 1.0, units_m);
            lengthDimension_1.getLength().createDesignParameter("HalfHeight");

            // end sketch
            sketch_0.markFeatureForEdit();
            cadModel_0.getFeatureManager().stopSketchEdit(sketch_0, true);
            sketch_0.setIsUptoDate(true);
            cadModel_0.getFeatureManager().rollForwardToEnd();

        }
        
        public void createNACA_Series() {

            Simulation sim = getActiveSimulation();
            
            // ask user for series definition and have drop-down for possible 3D-CAD models
            sim.print("  *** Opening the NACA series input panel...");
            NacaPanel tp = new NacaPanel(sim);
            boolean ok = promptUserForInput("NACA series", tp);
            boolean profile_ok = validateNacaSpec( tp.getNACA() );
            
            // create sketch if they hit ok
            if (ok && profile_ok) {
                String nacaString = tp.getNACA();
                String cadName = tp.getCAD();

                // screen message
                sim.println("\n  *** Building sketch of a NACA " + nacaString
                        + " airfoil using the XY-plane in 3D-CAD model: " + cadName + " ***  ");

                // build points from definition
                ArrayList<DoubleVector> all_coords = buildNACAProfile(nacaString);

                // make sketch
                Sketch sketch_0 = createSketch_NACA_blunt(all_coords, cadName);
                sketch_0.setPresentationName("Sketch: NACA " + nacaString);
            } else {
                if (!profile_ok) sim.print("Invalid NACA series specification...");
                sim.println("Exiting!!!");
            }
        }    
                
        // ===============================================
        // helper routines
        // ===============================================
        
        public boolean validateNacaSpec( String name ) {
            boolean isValid = false;
            
            // check 4-series names
            if (name.length()==4) {
                isValid = true;
            }
            
            // check 5-series names
            Collection<String> valid5series = new ArrayList<>( Arrays.asList("210", "220", "230", "240", "250", "221", "231", "241", "251") );
            String nameLeadingSpec = name.substring(0, 3);
            if (name.length()==5) {
                for (String valid_0 : valid5series) {
                    if (valid_0.equals(nameLeadingSpec)) {
                        isValid = true;
                        break;
                    }
                }
                
            }
            
            return isValid;
        }
        
        // <editor-fold defaultstate="collapsed" desc="public ArrayList<DoubleVector> buildNACAProfile( String digits )">
        public ArrayList<DoubleVector> buildNACAProfile( String digits ) {
            
            int naca_length = digits.length();
            
            // generate x-coordinates (symmetric spacing about x=0.5, small near LE/TE)
            double x[] = new double[31];
            for (int i=0; i<15; ++i) {
                double tmp = ((double) i)/15.0;
                x[i] = -0.01898*tmp + 0.73302*tmp*tmp - 0.21316*tmp*tmp*tmp;
                x[30-i] = 1.0-x[i];
            }
            x[15] = 0.5;
            
            // get airfoil surface (y-coordinates).
            // upper: back to front
            ArrayList<DoubleVector> all_coords = new ArrayList<>();
            for (int i=30; i>=0; --i) {
                double[] xy;
                if (naca_length == 5) {
                    xy = NACA5_coord( digits, x[i], "upper" );
                } else {
                    xy = NACA4_coord( digits, x[i], "upper" );
                }
                all_coords.add( new DoubleVector(xy) );
            }
            // upper: front to back (skip first point)
            for (int i=1; i<31; ++i) {
                double[] xy;
                if (naca_length == 5) {
                    xy = NACA5_coord( digits, x[i], "lower" );
                } else {
                    xy = NACA4_coord( digits, x[i], "lower" );
                }
                all_coords.add( new DoubleVector(xy) );
            }
            
            return all_coords;
        }
        //</editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc="public double[] NACA4_coord( String digits, double x, String surface )">
        public double[] NACA4_coord( String digits, double x, String surface ) {

            // get coefficients from NACA string
            double m = ( (double) Character.getNumericValue(digits.charAt(0)))/100.0;
            double p = ( (double) Character.getNumericValue(digits.charAt(1)))/10.0;
            double t = Double.parseDouble(digits.substring(2))/100.0;
            
            // get yt
            double yt = 5.0*t*(0.2969*Math.sqrt(x)-0.1260*x-0.3516*x*x+0.2843*x*x*x-0.1015*x*x*x*x);
            
            // get mean camber line
            double yc;
            if (x < p) {
                yc = m/(p*p)*(2.0*p*x-x*x);
            } else {
                yc = m/((1.0-p)*(1.0-p))*( (1.0-2.0*p)+2.0*p*x-x*x);
            }
            
            // get dyc/dx
            double dyc_dx;
            if (x < p) {
                dyc_dx = 2.0*m/(p*p)*(p-x);
            } else {
                dyc_dx = 2.0*m/((1.0-p)*(1.0-p))*(p-x);
            }
            double theta = Math.atan(dyc_dx);
            
            // calculate coords
            double[] coord = new double[2];
            if (surface.equalsIgnoreCase("upper")) {
                coord[0] = x  - yt*Math.sin(theta);
                coord[1] = yc + yt*Math.cos(theta);
            } else {
                coord[0] = x  + yt*Math.sin(theta);
                coord[1] = yc - yt*Math.cos(theta);
            }
            
            return coord;
        }
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc="public double[] NACA5_coord( String digits, double x, String surface )">
        public double[] NACA5_coord( String digits, double x, String surface ) {

            // get coefficients from NACA string
            // see naca-report-537.pdf
            // order {210,220,230,240,250,211,221,231,241,251}; 211 does not exist but added for indexing ease
            double[] m_all = new double[] {0.0580, 0.1260, 0.2025, 0.2900, 0.3910, 0.0, 0.130, 0.217, 0.318, 0.441};
            double[] k1_all = new double[] {361.40, 51.640, 15.957, 6.643, 3.230, 0.0, 51.990, 15.793, 6.520, 3.191};
            double[] k2k1_all = new double[] {0.0, 0.000764, 0.00677, 0.0303, 0.1355};
            
            int camber_index = Character.getNumericValue(digits.charAt(1));
            int reflex_index = Character.getNumericValue(digits.charAt(2));
            
            double m  =  m_all[camber_index-1 + reflex_index*5];
            double k1 = k1_all[camber_index-1 + reflex_index*5];
            double k2k1 = k2k1_all[camber_index-1];
            double p = ( (double) Character.getNumericValue(digits.charAt(1)))*0.05;
            double t = Double.parseDouble(digits.substring(3))/100.0;
            
            // get yt
            double yt = 5.0*t*(0.2969*Math.sqrt(x)-0.1260*x-0.3516*x*x+0.2843*x*x*x-0.1015*x*x*x*x);
            
            // get mean camber line
            double yc;
            if (reflex_index == 0) {
                if (x < p) {
                    yc = (k1/6.0)*( x*x*x - 3.0*m*x*x + m*m*x*(3.0-m) );
                } else {
                    yc = (k1/6.0)*m*m*m*(1.0-x);
                }
            } else {
                if (x < p) {
                    yc = (k1/6.0)*(      Math.pow(x-m,3.0) - k2k1*Math.pow(1.0-m,3.0)*x + m*m*m*(1.0-x) );
                } else {
                    yc = (k1/6.0)*( k2k1*Math.pow(x-m,3.0) - k2k1*Math.pow(1.0-m,3.0)*x + m*m*m*(1.0-x) );
                }
            }
            
            
            // get dyc/dx
            double dyc_dx;
            if (reflex_index == 0) {
                if (x < p) {
                    dyc_dx = (k1/6.0)*( 3.0*x*x - 6.0*m*x + m*m*(3.0-m) );
                } else {
                    dyc_dx = (k1/6.0)*m*m*m*(-1.0);
                }
            } else {
                if (x < p) {
                    dyc_dx = (k1/6.0)*(      3.0*Math.pow(x-m,2.0) - k2k1*Math.pow(1.0-m,3.0) + m*m*m*(-1.0) );
                } else {
                    dyc_dx = (k1/6.0)*( k2k1*3.0*Math.pow(x-m,2.0) - k2k1*Math.pow(1.0-m,3.0) + m*m*m*(-1.0) );
                }
            }
            double theta = Math.atan(dyc_dx);
            
            // calculate coords
            double[] coord = new double[2];
            if (surface.equalsIgnoreCase("upper")) {
                coord[0] = x  - yt*Math.sin(theta);
                coord[1] = yc + yt*Math.cos(theta);
            } else {
                coord[0] = x  + yt*Math.sin(theta);
                coord[1] = yc - yt*Math.cos(theta);
            }
            
            return coord;
        }
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc="public Sketch createSketch_NACA_blunt( ArrayList<DoubleVector> allPoints, String cadName )">
        public Sketch createSketch_NACA_blunt( ArrayList<DoubleVector> allPoints, String cadName ) {
            
            // preliminaries
            DoubleVector TE_upper = allPoints.get(0);
            DoubleVector TE_lower = allPoints.get( allPoints.size()-1 );
            
            // get sim and 3D-CAD
            Simulation sim = getActiveSimulation();
            CadModel cadModel_0 = ((CadModel) sim.get(SolidModelManager.class).getObject(cadName));

            CanonicalSketchPlane canonicalSketchPlane_0 = ((CanonicalSketchPlane) cadModel_0.getFeature("XY"));
            Sketch sketch_3 = cadModel_0.getFeatureManager().createSketch(canonicalSketchPlane_0);

            // start sketching
            cadModel_0.getFeatureManager().startSketchEdit(sketch_3);

            // build points for blunt TE
            PointSketchPrimitive TE_upper_point = sketch_3.createPoint(TE_upper);
            PointSketchPrimitive TE_lower_point = sketch_3.createPoint(TE_lower);

            // connect with line
            LineSketchPrimitive lineSketchPrimitive_1 = sketch_3.createLine(TE_upper_point, TE_lower_point);
            sketch_3.createVerticalConstraint(lineSketchPrimitive_1);

            // build spline
            DoubleVector airfoilSpline = allPoints.get(0);
            for (int i=1; i<allPoints.size(); ++i) {
                airfoilSpline.addAll( allPoints.get(i) );
            }
            sketch_3.createSpline(true, TE_upper_point, true, TE_lower_point, airfoilSpline );

            // finalize
            sketch_3.markFeatureForEdit();
            cadModel_0.getFeatureManager().stopSketchEdit(sketch_3, true);
            sketch_3.setIsUptoDate(true);
            cadModel_0.getFeatureManager().rollForwardToEnd();
            
            return sketch_3;

        }
        // </editor-fold>

    }
}
