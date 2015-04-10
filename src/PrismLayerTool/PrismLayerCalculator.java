package PrismLayerTool;

// STAR-CCM+ macro: PrismLayerCalculator.java


import star.common.*;
import java.text.NumberFormat;

public class PrismLayerCalculator extends StarMacro {

    public class PrismLayerCalcFrame extends javax.swing.JFrame {

        Simulation _sim;

        // constructors
        // ============================================
        public PrismLayerCalcFrame(Simulation sim) {
            _sim = sim;
            initComponents();
        }

        public PrismLayerCalcFrame() {
            initComponents();
        }

        // output writer
        // ============================================
        private void log(String msg) {
            _sim.println("[PrismLayerCalc]: " + msg);
        }
        
        private void logMessage(int N, double[] vals, int mode) {
            double lastCell;
            log("=========================\n\n");
            switch (mode) {
                case 0: // GP
                    lastCell = vals[1] * Math.pow(vals[2], (double) (N - 1));
                    log("Prism Layer Properties: Geometric Progress");
                    log("  Num Layers: " + N);
                    log("  Total Thickness: " + vals[0]);
                    log("  Near Wall Thickness: " + vals[1]);
                    log("  Stretching Ratio: " + vals[2]);
                    log("  The last prism layer thicknes is: " + lastCell);
                    break;
                case 1: // HT
                    double sn1 = (1.0 + Math.tanh(-vals[2] * (1.0 - ((double) N - 1) / ((double) N))) / Math.tanh(vals[2]));
                    double s2 = (1.0 + Math.tanh(-vals[2] * (1.0 - 2.0 / ((double) N))) / Math.tanh(vals[2]));
                    double s1 = vals[1] / vals[0];
                    double nearWallStretch = (s2 - s1) / s1;
                    lastCell = vals[0] * (1.0 - sn1);
                    log("Prism Layer Properties: Hyperbolic Tangent");
                    log("  Num Layers: " + N);
                    log("  Total Thickness: " + vals[0]);
                    log("  Near Wall Thickness: " + vals[1]);
                    log("  Stretching Parameter: " + vals[2]);
                    log("  Near wall stretching ratio: " + nearWallStretch);
                    log("  The last prism layer thicknes is: " + lastCell);
                    break;
            }
        }

        // getters
        // ============================================
        private int getStretchingFunc() {
            return stretchCombo.getSelectedIndex();
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

        private double getNearWallThickness() {
            return Double.parseDouble(nearWallBox.getText());
        }

        private double getStretchingValue() {
            return Double.parseDouble(stretchBox.getText());
        }

        // utility functions
        private void updateTextValues(int Layers, double[] other) {
            numLayersBox.setText( Integer.toString(Layers) );
            totalThicknessBox.setText( String.format("%.3f",Double.toString(other[0])) );
            nearWallBox.setText( String.format("%.3f",Double.toString(other[1])) );
            stretchBox.setText( String.format("%.3f",Double.toString(other[2])) );
        }

        // prism layer related calcs
        // ============================================
        private double calcNearWallThickness(int N, double totalThick, double stretch, int mode) {
            double val = 0.0;
            switch (mode) {
                case 0: // GP
                    val = calcNearWallThicknessGP(N, totalThick, stretch);
                    break;
                case 1: //HT
                    val = calcNearWallThicknessHT(N, totalThick, stretch);
                    break;
            }
            return val;
        }

        private double calcStretchingRatio(int N, double totalThick, double nearWall, int mode) {
            double val = 0.0;
            switch (mode) {
                case 0: // GP
                    val = calcStretchingRatioGP(N, totalThick, nearWall);
                    break;
                case 1: //HT
                    val = calcStretchingRatioHT(N, totalThick, nearWall);
                    break;
            }
            return val;

        }

        private double calcNearWallThicknessHT(int N, double totalThick, double stretch) {
            return totalThick * (1.0 + Math.tanh(-stretch * (1.0 - 1.0 / ((double) N))) / Math.tanh(stretch));
        }

        private double calcNearWallThicknessGP(int N, double totalThick, double stretch) {
            return totalThick * (stretch - 1.0) / (Math.pow(stretch, (double) N) - 1.0);
        }

        private double calcStretchingRatioGP(int N, double totalThick, double nearWall) {
            double stretch = 1.5;
            double err = 1.0;
            while (err > 0.00001) {
                err = stretch;
                stretch = Math.pow((totalThick / nearWall) * (stretch - 1.0) + 1.0, 1.0 / ((double) N));
                err = Math.abs(err - stretch);
            }
            return stretch;
        }

        private double calcStretchingRatioHT(int N, double totalThick, double nearWall) {
            double Fs = 1.0;
            double s1 = nearWall / totalThick;
            double err = 1.0;
            while (err > 0.00001) {
                err = Fs;
                Fs = -1.0 * atanh((s1 - 1.0) * Math.tanh(Fs)) / (1.0 - 1.0 / ((double) N));
                err = Math.abs(err - Fs);
            }
            return Fs;
        }

        // math utility
        // ======================================================
        private double atanh(double x) {
            return 0.5 * Math.log((1.0 + x) / (1.0 - x));
        }

        // Code generated by Netbeans for JFrame GUI
        @SuppressWarnings("unchecked") 
        // <editor-fold defaultstate="collapsed" desc="Generated Code">
        private void initComponents() {

            jLabel1 = new javax.swing.JLabel();
            jLabel2 = new javax.swing.JLabel();
            jLabel3 = new javax.swing.JLabel();
            jLabel4 = new javax.swing.JLabel();
            jLabel5 = new javax.swing.JLabel();
            jLabel6 = new javax.swing.JLabel();
            jLabel7 = new javax.swing.JLabel();
            jSeparator1 = new javax.swing.JSeparator();
            jSeparator2 = new javax.swing.JSeparator();
            stretchCombo = new javax.swing.JComboBox();
            distModeCombo = new javax.swing.JComboBox();
            numLayersBox = new javax.swing.JFormattedTextField(NumberFormat.getIntegerInstance());
            totalThicknessBox = new javax.swing.JFormattedTextField(NumberFormat.getNumberInstance());
            nearWallBox = new javax.swing.JFormattedTextField(NumberFormat.getNumberInstance());
            stretchBox = new javax.swing.JFormattedTextField(NumberFormat.getNumberInstance());
            closeButton = new javax.swing.JButton();
            calcButton = new javax.swing.JButton();

            setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
            setLocationByPlatform(true);
            setLocationRelativeTo(null);
            setMaximumSize(new java.awt.Dimension(890, 700));
            setMinimumSize(new java.awt.Dimension(445, 350));

            jLabel1.setFont(new java.awt.Font("SansSerif", 1, 16)); // NOI18N
            org.openide.awt.Mnemonics.setLocalizedText(jLabel1, "Prism Layer Calculator"); // NOI18N

            jLabel2.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
            org.openide.awt.Mnemonics.setLocalizedText(jLabel2, "Stretching Function: "); // NOI18N

            jLabel3.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
            org.openide.awt.Mnemonics.setLocalizedText(jLabel3, "Distribution Mode: "); // NOI18N

            jLabel4.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
            org.openide.awt.Mnemonics.setLocalizedText(jLabel4, "Number of Layers: "); // NOI18N

            jLabel5.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
            org.openide.awt.Mnemonics.setLocalizedText(jLabel5, "Total Thickness:"); // NOI18N

            jLabel6.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
            org.openide.awt.Mnemonics.setLocalizedText(jLabel6, "Near Wall Thickness:"); // NOI18N

            jLabel7.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
            org.openide.awt.Mnemonics.setLocalizedText(jLabel7, "Stretching Parameter:"); // NOI18N

            stretchCombo.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
            stretchCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Geometric Progression", "Hyperbolic Tangent"}));
            stretchCombo.setSelectedIndex(0);

            distModeCombo.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
            distModeCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Stretch Factor", "Wall Thickness"}));
            distModeCombo.setSelectedIndex(0);
            distModeCombo.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    distModeComboActionPerformed(evt);
                }
            });

            numLayersBox.setText("2"); // NOI18N
            numLayersBox.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
            numLayersBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    numLayersBoxActionPerformed(evt);
                }
            });

            totalThicknessBox.setText("1.0"); // NOI18N
            totalThicknessBox.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N

            nearWallBox.setText("0.4"); // NOI18N
            nearWallBox.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
            nearWallBox.setEditable(false);

            stretchBox.setText("1.5"); // NOI18N
            stretchBox.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N

            closeButton.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
            org.openide.awt.Mnemonics.setLocalizedText(closeButton, "Close"); // NOI18N
            closeButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    closeButtonActionPerformed(evt);
                }
            });

            calcButton.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
            org.openide.awt.Mnemonics.setLocalizedText(calcButton, "Calculate"); // NOI18N
            calcButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    calcButtonActionPerformed(evt);
                }
            });

            javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
            getContentPane().setLayout(layout);
            layout.setHorizontalGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1)
                    .addComponent(jSeparator2)
                    .addGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(stretchCombo, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(distModeCombo, javax.swing.GroupLayout.Alignment.TRAILING, 0, 200, Short.MAX_VALUE)
                    .addComponent(numLayersBox, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(totalThicknessBox, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(nearWallBox)
                    .addComponent(stretchBox)))
                    .addGroup(layout.createSequentialGroup()
                    .addComponent(jLabel1)
                    .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addGap(0, 201, Short.MAX_VALUE)
                    .addComponent(calcButton, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(18, 18, 18)
                    .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addContainerGap()));
            layout.setVerticalGroup(
                    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jLabel1)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(stretchCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                    .addGap(2, 2, 2)
                    .addComponent(jLabel3)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(numLayersBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(distModeCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(totalThicknessBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nearWallBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(stretchBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                    .addGap(18, 18, 18)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(closeButton)
                    .addComponent(calcButton))
                    .addContainerGap(19, Short.MAX_VALUE)));

            pack();
        } // </editor-fold>                

        private void numLayersBoxActionPerformed(java.awt.event.ActionEvent evt) {
        }

        private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {
            setVisible(false);
        }

        private void calcButtonActionPerformed(java.awt.event.ActionEvent evt) {
            // TODO add your handling code here:
            int distMode = distModeCombo.getSelectedIndex();
            int stretchFun = stretchCombo.getSelectedIndex();

            int N = getNumLayers();
            double[] vals = {1.0, 1.0, 1.0};
            vals[0] = getTotalThickness();
            vals[1] = getNearWallThickness();
            vals[2] = getStretchingValue();

            switch (distMode) {
                case 0: // Stretch Factor
                    vals[1] = calcNearWallThickness(N, vals[0], vals[2], stretchFun);
                    break;
                case 1: // Near Wall Thickness
                    vals[2] = calcStretchingRatio(N, vals[0], vals[1], stretchFun);
                    break;
            }
            updateTextValues(N, vals);
            logMessage(N, vals, stretchFun);
        }

        private void distModeComboActionPerformed(java.awt.event.ActionEvent evt) {
            // TODO add your handling code here:
            int distMode = distModeCombo.getSelectedIndex();
            switch (distMode) {
                case 0:
                    nearWallBox.setEditable(false);
                    stretchBox.setEditable(true);
                    break;
                case 1:
                    nearWallBox.setEditable(true);
                    stretchBox.setEditable(false);
                    break;
            }
        }
        // Variables declaration - do not modify                     
        private javax.swing.JButton calcButton;
        private javax.swing.JButton closeButton;
        private javax.swing.JComboBox distModeCombo;
        private javax.swing.JLabel jLabel1;
        private javax.swing.JLabel jLabel2;
        private javax.swing.JLabel jLabel3;
        private javax.swing.JLabel jLabel4;
        private javax.swing.JLabel jLabel5;
        private javax.swing.JLabel jLabel6;
        private javax.swing.JLabel jLabel7;
        private javax.swing.JSeparator jSeparator1;
        private javax.swing.JSeparator jSeparator2;
        private javax.swing.JFormattedTextField nearWallBox;
        private javax.swing.JFormattedTextField numLayersBox;
        private javax.swing.JFormattedTextField stretchBox;
        private javax.swing.JComboBox stretchCombo;
        private javax.swing.JFormattedTextField totalThicknessBox;
        // End of variables declaration                   
    }

    @Override
    public void execute() {

        // grab Simulation
        Simulation sim = getActiveSimulation();

        // launch PrismLayerCalculator Panel
        final PrismLayerCalcFrame calc = new PrismLayerCalcFrame(sim);
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                calc.setVisible(true);
            }
        });


    }
}
