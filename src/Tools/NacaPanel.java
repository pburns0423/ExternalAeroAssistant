package Tools;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collection;
import star.cadmodeler.SolidModelManager;
import star.common.CadModelBase;
import star.common.Simulation;

public class NacaPanel extends javax.swing.JPanel {

    Simulation sim;
    public NacaPanel(Simulation sim_0) {
        sim = sim_0;
        initComponents();
        setCADComboList();
    }
    
    public String getNACA() {
        return nacaText.getText();
    }
    
    public String getCAD() {
        return cadComboBox.getItemAt(cadComboBox.getSelectedIndex());
    }
    
    private void setCADComboList() {
        Collection<CadModelBase> cad_all = sim.get(SolidModelManager.class).getObjects();
        String[] cadNames = new String[cad_all.size()];
        int i=0;
        for (CadModelBase cad_0 : cad_all)
            cadNames[i++] = cad_0.getPresentationName();
        cadComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(cadNames));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="GUI Code">                          
    private void initComponents() {

        //NumberFormat numFormat = new DecimalFormat("#0000");
        cadLabel = new javax.swing.JLabel();
        nacaLabel = new javax.swing.JLabel();
        cadComboBox = new javax.swing.JComboBox<>();
        nacaText = new javax.swing.JTextField();

        org.openide.awt.Mnemonics.setLocalizedText(cadLabel, "3D-CAD model:"); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(nacaLabel,"NACA profile: "); // NOI18N

        cadComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "3D-CAD 1" }));

        nacaText.setText("0012"); // NOI18N


        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(nacaLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(nacaText, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cadLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cadComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(29, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cadLabel)
                    .addComponent(cadComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nacaLabel)
                    .addComponent(nacaText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>                                                              


    // Variables declaration - do not modify                     
    private javax.swing.JComboBox<String> cadComboBox;
    private javax.swing.JLabel cadLabel;
    private javax.swing.JLabel nacaLabel;
    private javax.swing.JTextField nacaText;
    // End of variables declaration                   
}
