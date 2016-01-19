package Tools;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import star.common.Simulation;

/**
 * The Prism Layer Calculator assists a user in determining the best possible
 * settings for their prism layer by calculating the dependent quantities
 * that a user is not specifying. As an example, if you specify the 
 * number of layers, stretching ratio and total thickness, this class
 * will calculate the first layer thickness.
 * 
 * @author peterb
 */
public class PrismLayerCalculator extends javax.swing.JFrame {

    Simulation _sim;
    boolean _debug = false;

    /**
     * Constructor for the prism layer calculator.
     * 
     * @param sim - Simulation object is needed for logging output.
     */
    public PrismLayerCalculator(Simulation sim) {
        _sim = sim;
        initComponents();
        printUsageMessage();
    }

    private void printUsageMessage() {
        _sim.println("");
        _sim.println("Welcome to the Prism Layer Calculator!");
        _sim.println("Please use a consistent length unit for the total thickness and near-wall thickness values");
        _sim.println("All other values are dimensionless");
        _sim.println("");

    }

    private void log(String msg) {
        _sim.println("[PrismLayerCalc]: " + msg);
    }

    private void debug(String msg) {
        if (_debug) {
            log(msg);
        }
    }

    private int getStretchingFunc() {
        return stretchFuncCombo.getSelectedIndex();
    }

    private int getDistributionMode() {
        return distModeCombo.getSelectedIndex();
    }

    private int getNumLayers() {
        return Integer.parseInt(numLayersBox.getText());
    }

    private double getTotalThickness() {
        return Double.parseDouble(totalThicknessBox.getText());
    }

    private double getStretchingValue() {
        return Double.parseDouble(stretchParameterBox.getText());
    }

    private double getNearWallThickness() {
        return Double.parseDouble(nearWallBox.getText());
    }

    private double getThicknessRatioValue() {
        return Double.parseDouble(thicknessRatioBox.getText());
    }

    private void updateTextValues(int Layers, double[] other) {
        numLayersBox.setText(Integer.toString(Layers));
        totalThicknessBox.setText(String.format("%.3f", other[0]));
        stretchParameterBox.setText(String.format("%.3f", other[1]));
        nearWallBox.setText(String.format("%.6f", other[2]));
        thicknessRatioBox.setText(String.format("%.3f", other[3]));
    }

    /**
     * Wrapper method for cases when the distribution mode is set to Stretch Factor
     * 
     * @param N - Number of prism layers
     * @param totalThick - Total prism layer thickness
     * @param stretch - stretching parameter (geometric or hyperbolic)
     * @param mode - specifies the functional form of the stretching
     * 
     * @return Two values specifying the near-wall thickness and the thickness ratio
     */
    private double[] calcFromStretchingRatio(int N, double totalThick, double stretch, int mode) {
        double[] vals = {0.0, 0.0};
        switch (mode) {
            case 0: // GP
                debug("Calculating based on Stretching Ratio using Geometric Progression");
                vals[0] = calcNearWallThicknessFromStretchingGP(N, totalThick, stretch);
                vals[1] = calcThicknessRatioFromStretchingRatioGP(N, stretch);
                break;
            case 1: //HT
                debug("Calculating based on Stretching Ratio using Hyperbolic Tangent");
                vals[0] = calcNearWallThicknessFromStretchingHT(N, totalThick, stretch);
                vals[1] = calcThicknessRatioFromStretchingRatioHT(N, stretch);
                break;
        }
        return vals;
    }

    /**
     * Wrapper method for cases when the distribution mode is set to Wall Thickness
     * 
     * @param N - Number of prism layers
     * @param totalThick - Total prism layer thickness
     * @param nearWall - Near-wall layer thickness
     * @param mode - specifies the functional form of the stretching
     * 
     * @return Two values specifying the stretching ratio and the thickness ratio
     */
    private double[] calcFromNearWallThickness(int N, double totalThick, double nearWall, int mode) {
        double[] vals = {0.0, 0.0};
        switch (mode) {
            case 0: // GP
                debug("Calculating based on Near-Wall Thickness using Geometric Progression");
                vals[0] = calcStretchingRatioFromNearWallThicknessGP(N, totalThick, nearWall);
                vals[1] = calcThicknessRatioFromStretchingRatioGP(N, vals[0]);
                break;
            case 1: //HT
                debug("Calculating based on Near-Wall Thickness using Hyperbolic Tangent");
                vals[0] = calcStretchingRatioFromNearWallThicknessHT(N, totalThick, nearWall);
                vals[1] = calcThicknessRatioFromStretchingRatioHT(N, vals[0]);
                break;
        }
        return vals;

    }

    /**
     * Wrapper method for cases when the distribution mode is set to Thickness Ratio
     * 
     * @param N - Number of prism layers
     * @param totalThick - Total prism layer thickness
     * @param thicknessRatio - ratio of thickness between the first and last prism layers
     * @param mode - specifies the functional form of the stretching
     * 
     * @return Two values specifying the stretching ratio and the near-wall thickness
     */
    private double[] calcFromThicknessRatio(int N, double totalThick, double thicknessRatio, int mode) {
        double[] vals = {0.0, 0.0};
        switch (mode) {
            case 0: // GP
                debug("Calculating based on Thickness Ratio using Geometric Progression");
                vals[0] = calcStretchingRatioFromThicknessRatioGP(N, thicknessRatio);
                vals[1] = calcNearWallThicknessFromStretchingGP(N, totalThick, vals[0]);
                break;
            case 1: //HT
                debug("Calculating based on Thickness Ratio using Hyperbolic Tangent");
                vals[0] = calcStretchingRatioFromThicknessRatioHT(N, thicknessRatio);
                vals[1] = calcNearWallThicknessFromStretchingHT(N, totalThick, vals[0]);
                break;
        }
        return vals;

    }
    
    private double calcNearWallThicknessFromStretchingGP(int N, double totalThick, double stretch) {
        double Nd = (double) N;
        return totalThick * (stretch - 1.0) / (Math.pow(stretch, Nd) - 1.0);
    }

    private double calcStretchingRatioFromNearWallThicknessGP(int N, double totalThick, double nearWall) {
        double Nd = (double) N;
        double fac = 1.0 / Nd;
        double stretch = 1.5;
        for (int i = 0; i < 20; i++) {
            stretch = Math.pow((totalThick / nearWall) * (stretch - 1.0) + 1.0, fac);
        }
        return stretch;
    }

    private double calcStretchingRatioFromThicknessRatioGP(int N, double thicknessRatio) {
        double Nd = (double) N;
        return Math.exp(Math.log(thicknessRatio) / (Nd - 1.0));
    }

    private double calcThicknessRatioFromStretchingRatioGP(int N, double stretch) {
        double Nd = (double) N;
        return Math.pow(stretch, Nd - 1.0);
    }

    private double calcNearWallThicknessFromStretchingHT(int N, double totalThick, double stretch) {
        double Nd = (double) N;
        double fac = 1.0 / Nd - 1.0;
        return totalThick * (1.0 + Math.tanh(stretch * fac) / Math.tanh(stretch));
    }

    private double calcStretchingRatioFromNearWallThicknessHT(int N, double totalThick, double nearWall) {
        double Nd = (double) N;
        double fac = 1.0 / Nd - 1.0;
        double Fs = 1.0;
        double s1 = nearWall / totalThick;
        for (int i = 0; i < 50; i++) {
            Fs = atanh((s1 - 1.0) * Math.tanh(Fs)) / fac;
        }
        return Fs;
    }

    private double calcStretchingRatioFromThicknessRatioHT(int N, double thicknessRatio) {
        double omega = 0.05; // under-relaxation for bad initial guesses
        double Fs_old = 1.0;
        double Fs_new = 2.0;
        double Gold = calcThicknessRatioFromStretchingRatioHT(N, Fs_old) - thicknessRatio;
        double Gnew = calcThicknessRatioFromStretchingRatioHT(N, Fs_new) - thicknessRatio;
        double slope;
        for (int i = 0; i < 50; i++) {
            slope = (Fs_new - Fs_old) / (Gnew - Gold);
            Fs_old = Fs_new;
            Gold = Gnew;
            Fs_new = Fs_new + slope * (0.0 - Gnew) * omega;
            Gnew = calcThicknessRatioFromStretchingRatioHT(N, Fs_new) - thicknessRatio;
            omega += 0.01; // slowly increase to help convergence
        }
        return Fs_new;
    }

    private double calcThicknessRatioFromStretchingRatioHT(int N, double Fs) {
        double Nd = (double) N;
        double s1 = 1.0 + Math.tanh(Fs * (1.0 / Nd - 1.0)) / Math.tanh(Fs);
        double sNm1 = 1.0 + Math.tanh(-Fs / Nd) / Math.tanh(Fs);
        return (1.0 - sNm1) / s1;
    }

    private double atanh(double x) {
        return 0.5 * Math.log((1.0 + x) / (1.0 - x));
    }

    // <editor-fold defaultstate="collapsed" desc="GUI Code">                          
    private void initComponents() {

        // labels
        prismLayerToolTitle = new javax.swing.JLabel();
        stretchFuncLabel = new javax.swing.JLabel();
        distModeLabel = new javax.swing.JLabel();
        numLayersLabel = new javax.swing.JLabel();
        totalThicknessLabel = new javax.swing.JLabel();
        stretchParameterLabel = new javax.swing.JLabel();
        nearWallLabel = new javax.swing.JLabel();
        thicknessRatioLabel = new javax.swing.JLabel();

        // separators
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();

        // combo boxes
        stretchFuncCombo = new javax.swing.JComboBox();
        distModeCombo = new javax.swing.JComboBox();

        // text boxes holding numeric strings
        NumberFormat numFormat = new DecimalFormat("#0.000");
        numLayersBox = new javax.swing.JFormattedTextField(NumberFormat.getIntegerInstance());
        totalThicknessBox = new javax.swing.JFormattedTextField(NumberFormat.getNumberInstance());
        stretchParameterBox = new javax.swing.JFormattedTextField(NumberFormat.getNumberInstance());
        nearWallBox = new javax.swing.JFormattedTextField(NumberFormat.getNumberInstance());
        thicknessRatioBox = new javax.swing.JFormattedTextField(numFormat);

        // buttons
        closeButton = new javax.swing.JButton();
        calculateButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setLocationByPlatform(true);
        setMinimumSize(new java.awt.Dimension(445, 350));

        prismLayerToolTitle.setFont(new java.awt.Font("SansSerif", 1, 16)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(prismLayerToolTitle, "Prism Layer Calculator"); // NOI18N

        stretchFuncLabel.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(stretchFuncLabel, "Stretching Function:"); // NOI18N

        distModeLabel.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(distModeLabel, "Distribution Mode:"); // NOI18N

        numLayersLabel.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(numLayersLabel, "Number of Layers:"); // NOI18N

        totalThicknessLabel.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(totalThicknessLabel, "Total Thickness:"); // NOI18N

        stretchParameterLabel.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(stretchParameterLabel, "Stretching Parameter:"); // NOI18N

        nearWallLabel.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(nearWallLabel, "Near-wall Thickness:"); // NOI18N

        thicknessRatioLabel.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(thicknessRatioLabel, "Thickness Ratio:"); // NOI18N

        stretchFuncCombo.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        stretchFuncCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Geometric Progression", "Hyperbolic Tangent"}));
        stretchFuncCombo.setSelectedIndex(0);

        distModeCombo.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        distModeCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Stretch Factor", "Wall Thickness", "Thickness Ratio"}));
        distModeCombo.setSelectedIndex(0);
        distModeCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                distModeComboActionPerformed(evt);
            }
        });

        numLayersBox.setText("2"); // NOI18N
        numLayersBox.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N

        totalThicknessBox.setText("1.0"); // NOI18N
        totalThicknessBox.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N

        stretchParameterBox.setText("1.5"); // NOI18N
        stretchParameterBox.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        stretchParameterBox.setEditable(true);

        nearWallBox.setText("0.4"); // NOI18N
        nearWallBox.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        nearWallBox.setEditable(false);

        thicknessRatioBox.setText("1.5"); // NOI18N
        thicknessRatioBox.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        thicknessRatioBox.setEditable(false);

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
                .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jSeparator1)
                                .addComponent(jSeparator2)
                                .addComponent(jSeparator3)
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(prismLayerToolTitle)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                .addComponent(thicknessRatioLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(totalThicknessLabel, javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(numLayersLabel, javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(stretchParameterLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(nearWallLabel, javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(distModeLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(stretchFuncLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 43, Short.MAX_VALUE)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                .addComponent(stretchParameterBox, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                                                .addComponent(nearWallBox)
                                                .addComponent(thicknessRatioBox)
                                                .addComponent(stretchFuncCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(numLayersBox)
                                                .addComponent(distModeCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(totalThicknessBox))))
                        .addContainerGap())
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(calculateButton, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(14, 14, 14))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(prismLayerToolTitle)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(stretchFuncCombo)
                                .addComponent(stretchFuncLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(distModeCombo)
                                .addComponent(distModeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(numLayersLabel)
                                .addComponent(numLayersBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(totalThicknessBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(totalThicknessLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(stretchParameterBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(stretchParameterLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                .addComponent(nearWallBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(nearWallLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(thicknessRatioLabel)
                                .addComponent(thicknessRatioBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(calculateButton)
                                .addComponent(closeButton))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>                                                                

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {
        setVisible(false);
    }

    private void calculateButtonActionPerformed(java.awt.event.ActionEvent evt) {
        int distMode = getDistributionMode();
        int stretchFun = getStretchingFunc();

        int N = getNumLayers();
        double[] vals = {1.0, 1.0, 1.0, 1.0};
        vals[0] = getTotalThickness();
        vals[1] = getStretchingValue();
        vals[2] = getNearWallThickness();
        vals[3] = getThicknessRatioValue();

        // update other two values
        double[] tmpVals;
        switch (distMode) {
            case 0: // Stretch Factor
                tmpVals = calcFromStretchingRatio(N, vals[0], vals[1], stretchFun);
                vals[2] = tmpVals[0];
                vals[3] = tmpVals[1];
                break;
            case 1: // Near Wall Thickness
                tmpVals = calcFromNearWallThickness(N, vals[0], vals[2], stretchFun);
                vals[1] = tmpVals[0];
                vals[3] = tmpVals[1];
                break;
            case 2: // Thickness Ratio
                tmpVals = calcFromThicknessRatio(N, vals[0], vals[3], stretchFun);
                vals[1] = tmpVals[0];
                vals[2] = tmpVals[1];
                break;
        }
        updateTextValues(N, vals);
    }

    private void distModeComboActionPerformed(java.awt.event.ActionEvent evt) {
        int distMode = distModeCombo.getSelectedIndex();
        switch (distMode) {
            case 0:
                stretchParameterBox.setEditable(true);
                nearWallBox.setEditable(false);
                thicknessRatioBox.setEditable(false);
                break;
            case 1:
                stretchParameterBox.setEditable(false);
                nearWallBox.setEditable(true);
                thicknessRatioBox.setEditable(false);
                break;
            case 2:
                stretchParameterBox.setEditable(false);
                nearWallBox.setEditable(false);
                thicknessRatioBox.setEditable(true);
                break;
        }
    }

    // Variables declaration - do not modify                     
    private javax.swing.JButton calculateButton;
    private javax.swing.JButton closeButton;
    private javax.swing.JComboBox distModeCombo;
    private javax.swing.JComboBox stretchFuncCombo;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JLabel prismLayerToolTitle;
    private javax.swing.JLabel stretchFuncLabel;
    private javax.swing.JLabel distModeLabel;
    private javax.swing.JLabel numLayersLabel;
    private javax.swing.JLabel totalThicknessLabel;
    private javax.swing.JLabel stretchParameterLabel;
    private javax.swing.JLabel nearWallLabel;
    private javax.swing.JLabel thicknessRatioLabel;
    private javax.swing.JFormattedTextField numLayersBox;
    private javax.swing.JFormattedTextField totalThicknessBox;
    private javax.swing.JFormattedTextField stretchParameterBox;
    private javax.swing.JFormattedTextField nearWallBox;
    private javax.swing.JFormattedTextField thicknessRatioBox;
    // End of variables declaration                   
}
