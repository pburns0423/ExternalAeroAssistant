package Tools;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import star.common.Simulation;

/**
 * The Wall Y+ Calculator assists a user in determining the near-wall spacing
 * for their prism layer based a freestream Reynolds number and a correlation
 * for the skin-friction coefficient.
 * 
 * @author peterb
 */
public class WallYpCalculator extends javax.swing.JFrame {

    Simulation _sim;
    int _method;
    boolean _debug = false;

    /**
     * Constructor for the wall y+ calculator.
     * 
     * @param sim - Simulation object is needed for logging output.
     */
    public WallYpCalculator(Simulation sim) {
        _sim = sim;
        _method = 0;
        initComponents();
        printUsageMessage();
    }

    private void printUsageMessage() {
        _sim.println("");
        _sim.println("Welcome to the Wall Y+ Calculator!");
        _sim.println("Please use a consistent unit system for all input fields, e.g. - MKS");
        _sim.println("Note - The near-wall thickness takes into account the fact that the cell thickness");
        _sim.println("       is twice the wall distance to the near-wall centroid.");
        _sim.println("");

    }

    private void log(String msg) {
        _sim.println("[WallY+Calc]: " + msg);
    }

    private void debug(String msg) {
        if (_debug) {
            log(msg);
        }
    }

    private double getFreestreamVelocity() {
        return Double.parseDouble(freestreamVelocityBox.getText());
    }

    private double getDensity() {
        return Double.parseDouble(densityBox.getText());
    }

    private double getDynamicViscosity() {
        return Double.parseDouble(viscosityBox.getText());
    }

    private double getReferenceLength() {
        return Double.parseDouble(refLengthBox.getText());
    }

    private double getTargetWallYp() {
        return Double.parseDouble(targetYpBox.getText());
    }

    /**
     * Allows a user to switch between different correlations for the skin
     * friction coefficient.
     * 
     * @param method 0 - Schlichting skin friction,
     *               1 - Prandtl (1927),            
     *               2 - ITTC (1957),              
     *               3 - Prandtl-Schlichting (1932)
     */
    public void setSkinFrictionMethod(int method) {
        _method = method;
        switch (_method) {
            case 1: // Prandtl (1927)
                log("Using the skin friction correlation of Prandtl (1927)");
                break;
            case 2: // ITTC (1957)
                log("Using the skin friction correlation of ITTC (1957)");
                break;
            case 3: //Prandtl-Schlichting (1932)
                log("Using the skin friction correlation of Prandtl-Schlichting (1932)");
                break;
            default: //Schlichting skin-friction
                log("Using the DEFAULT skin friction correlation, Schlichting (19XX)");
                break;
        }
    }

    /**
     * Prints a brief message to the output window with all of the available
     * correlations, both by name and equation
     */
    public void printAvailableSkinFrictionMethods() {
        _sim.println("");
        _sim.println("Available skin-friction correlations (using \"setSkinFrictionMethod( int method )\"): ");
        _sim.println("         1) Prandtl             (1927): 0.074*Math.pow( Re, -0.200 )");
        _sim.println("         2) ITTC                (1957): 0.075*Math.pow( Math.log10(Re)-2.000, -2.000 )");
        _sim.println("         3) Prandtl-Schlichting (1932): 0.455*Math.pow( Math.log10(Re), -2.580 )");
        _sim.println("   default) Schlichting         (19XX): Math.pow( 2.000*Math.log10(Re)-0.650, -2.300)");
        _sim.println("");
    }

    private void printNearWallThickness(double nearWall) {
        log("You calculated a near-wall cell thickness of: " + String.format("%.3e", nearWall));
    }

    /**
     * Calculates the skin friction coefficient, taking into account the user
     * specified correlation
     * 
     * @param Re - Reynolds number used in the correlation
     * @return Cf - Skin friction coefficient
     */
    private double calculateCf(double Re) {
        double Cf;
        switch (_method) {
            case 1: // Prandtl (1927)
                Cf = 0.074 * Math.pow(Re, -0.2);
                break;
            case 2: // ITTC (1957)
                Cf = 0.075 * Math.pow(Math.log10(Re) - 2.0, -2.0);
                break;
            case 3: //Prandtl-Schlichting (1932)
                Cf = 0.455 * Math.pow(Math.log10(Re), -2.58);
                break;
            default: //Schlichting skin-friction
                Cf = Math.pow(2.0 * Math.log10(Re) - 0.65, -2.30);
                break;
        }
        return Cf;
    }

    private void updateTextValues(double Re, double nearWall) {
        reynoldsBox.setText(String.format("%.3e", Re));
        nearWallThicknessBox.setText(String.format("%.3e", nearWall));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="GUI Code">                          
    private void initComponents() {

        // title
        wallYpToolTitle = new javax.swing.JLabel();

        // labels
        freestreamVelocityLabel = new javax.swing.JLabel();
        densityLabel = new javax.swing.JLabel();
        viscosityLabel = new javax.swing.JLabel();
        refLengthLabel = new javax.swing.JLabel();
        targetYpLabel = new javax.swing.JLabel();
        reynoldsLabel = new javax.swing.JLabel();
        nearWallThicknessLabel = new javax.swing.JLabel();

        // separators
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();
        jSeparator4 = new javax.swing.JSeparator();

        // text boxes
        NumberFormat doubleFormat = new DecimalFormat("#0.000");
        NumberFormat sciFormat = new DecimalFormat("0.###E0");
        freestreamVelocityBox = new javax.swing.JFormattedTextField(doubleFormat);
        densityBox = new javax.swing.JFormattedTextField(doubleFormat);
        viscosityBox = new javax.swing.JFormattedTextField(sciFormat);
        refLengthBox = new javax.swing.JFormattedTextField(doubleFormat);
        targetYpBox = new javax.swing.JFormattedTextField(doubleFormat);
        reynoldsBox = new javax.swing.JFormattedTextField(sciFormat);
        nearWallThicknessBox = new javax.swing.JFormattedTextField(sciFormat);

        // buttons
        closeButton = new javax.swing.JButton();
        calculateButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setLocationByPlatform(true);
        setMinimumSize(new java.awt.Dimension(445, 350));

        wallYpToolTitle.setFont(new java.awt.Font("SansSerif", 1, 16)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(wallYpToolTitle, "Wall Y+ Calculator"); // NOI18N

        freestreamVelocityLabel.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(freestreamVelocityLabel, "Freestream velocity:"); // NOI18N

        densityLabel.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(densityLabel, "Density:"); // NOI18N

        viscosityLabel.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(viscosityLabel, "Dynamic viscosity:"); // NOI18N

        refLengthLabel.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(refLengthLabel, "Reference length:"); // NOI18N

        targetYpLabel.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(targetYpLabel, "Target Y+ value:"); // NOI18N

        reynoldsLabel.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(reynoldsLabel, "Reynolds number:"); // NOI18N

        nearWallThicknessLabel.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(nearWallThicknessLabel, "Near-wall thickness:"); // NOI18N

        freestreamVelocityBox.setText("1.0"); // NOI18N
        freestreamVelocityBox.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N

        densityBox.setText("1.184"); // NOI18N
        densityBox.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N

        viscosityBox.setText("1.885E-5"); // NOI18N
        viscosityBox.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N

        refLengthBox.setText("1.0"); // NOI18N
        refLengthBox.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N

        targetYpBox.setText("1.0"); // NOI18N
        targetYpBox.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N

        reynoldsBox.setText("Press Calculate"); // NOI18N
        reynoldsBox.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        reynoldsBox.setEditable(false);

        nearWallThicknessBox.setText("Press Calculate"); // NOI18N
        nearWallThicknessBox.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        nearWallThicknessBox.setEditable(false);

        closeButton.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(closeButton, "Close"); // NOI18N
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        calculateButton.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(calculateButton, "Calculate"); // NOI18N
        calculateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                calculateButtonActionPerformed(evt);
            }
        });

        // LAYOUT
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(calculateButton, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(14, 14, 14))
                .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jSeparator1)
                                .addComponent(jSeparator3)
                                .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                        .addComponent(targetYpLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(densityLabel, javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(viscosityLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)
                                                        .addComponent(refLengthLabel, javax.swing.GroupLayout.Alignment.LEADING))
                                                .addComponent(freestreamVelocityLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 41, Short.MAX_VALUE)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                        .addComponent(freestreamVelocityBox)
                                                        .addComponent(densityBox, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                                                        .addComponent(viscosityBox)
                                                        .addComponent(refLengthBox))
                                                .addComponent(targetYpBox, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(reynoldsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(reynoldsBox, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(wallYpToolTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(nearWallThicknessLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(nearWallThicknessBox, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(jSeparator4))
                        .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(wallYpToolTitle)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(freestreamVelocityLabel)
                                .addComponent(freestreamVelocityBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(densityBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(densityLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(viscosityBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(viscosityLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                .addComponent(refLengthBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(refLengthLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(targetYpLabel)
                                .addComponent(targetYpBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(reynoldsLabel)
                                .addComponent(reynoldsBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(nearWallThicknessLabel)
                                .addComponent(nearWallThicknessBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(calculateButton)
                                .addComponent(closeButton))
                        .addContainerGap())
        );

        pack();
    }// </editor-fold>                                                                          

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {
        setVisible(false);
    }

    private void calculateButtonActionPerformed(java.awt.event.ActionEvent evt) {
        double Re, nearWall;
        double U, rho, mu, Lref, targetYp;
        double Cf;

        // get values from frame
        U = getFreestreamVelocity();
        rho = getDensity();
        mu = getDynamicViscosity();
        Lref = getReferenceLength();
        targetYp = getTargetWallYp();

        // calculate Re and Cf
        Re = rho * U * Lref / mu;
        Cf = calculateCf(Re);
        // U_tau = U*Math.sqrt( 0.5*Cf );

        // calculate near wall spacing
        //   *)factor of 2.0 to take into account that wall to cell-centroid 
        //     distance is half the near-wall layer thickness
        nearWall = 2.0 * targetYp * mu / (rho * U * Math.sqrt(0.5 * Cf));

        updateTextValues(Re, nearWall);
        printNearWallThickness(nearWall);
    }

    // Variables declaration - do not modify                     
    private javax.swing.JButton calculateButton;
    private javax.swing.JButton closeButton;
    private javax.swing.JFormattedTextField densityBox;
    private javax.swing.JLabel densityLabel;
    private javax.swing.JFormattedTextField freestreamVelocityBox;
    private javax.swing.JLabel freestreamVelocityLabel;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JFormattedTextField nearWallThicknessBox;
    private javax.swing.JLabel nearWallThicknessLabel;
    private javax.swing.JFormattedTextField refLengthBox;
    private javax.swing.JLabel refLengthLabel;
    private javax.swing.JFormattedTextField reynoldsBox;
    private javax.swing.JLabel reynoldsLabel;
    private javax.swing.JFormattedTextField targetYpBox;
    private javax.swing.JLabel targetYpLabel;
    private javax.swing.JFormattedTextField viscosityBox;
    private javax.swing.JLabel viscosityLabel;
    private javax.swing.JLabel wallYpToolTitle;
    // End of variables declaration                   
}
    
  
