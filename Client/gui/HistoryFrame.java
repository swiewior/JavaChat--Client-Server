package gui;

import client.History;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;

public class HistoryFrame extends javax.swing.JFrame {
		
	public History hist;
	private static final Logger LOG = Logger.getLogger( Logger.GLOBAL_LOGGER_NAME );
	

	
	public HistoryFrame() {		
		initComponents();
	}
	
	public HistoryFrame(History hist){
		this.hist = hist;
		initComponents();
		hist.FillTable(this);
	}
	
		@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents()
	{

		jLabel1 = new javax.swing.JLabel();
		jScrollPane1 = new javax.swing.JScrollPane();
		jTable1 = new javax.swing.JTable();
		jButton1 = new javax.swing.JButton();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setTitle("Chat History");
		setPreferredSize(new java.awt.Dimension(600, 350));

		jLabel1.setText("Plik historii: "+hist.filePath);

		jTable1.setModel(new javax.swing.table.DefaultTableModel(
			new Object [][] 
			{},
			new String [] {
				"Nadawca", "Wiadomość", "Odbiorca", "Czas"
			}
		)
		{
			Class[] types = new Class []
			{
				java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
			};
			boolean[] canEdit = new boolean []
			{
				false, false, false, false
			};

			public Class getColumnClass(int columnIndex)
			{
				return types [columnIndex];
			}

			public boolean isCellEditable(int rowIndex, int columnIndex)
			{
				return canEdit [columnIndex];
			}
		});
		jScrollPane1.setViewportView(jTable1);
		if (jTable1.getColumnModel().getColumnCount() > 0)
		{
			jTable1.getColumnModel().getColumn(0).setPreferredWidth(10);
			jTable1.getColumnModel().getColumn(1).setPreferredWidth(200);
			jTable1.getColumnModel().getColumn(2).setPreferredWidth(10);
			jTable1.getColumnModel().getColumn(3).setPreferredWidth(150);
		}

		jButton1.setText("Wyczyść");
		jButton1.addMouseListener(new java.awt.event.MouseAdapter()
		{
			public void mouseClicked(java.awt.event.MouseEvent evt)
			{
				jButton1MouseClicked(evt);
			}
		});

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
			layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
			.addGroup(layout.createSequentialGroup()
				.addContainerGap()
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
					.addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 576, Short.MAX_VALUE)
					.addGroup(layout.createSequentialGroup()
						.addComponent(jLabel1)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(jButton1)))
				.addContainerGap())
		);
		layout.setVerticalGroup(
			layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
			.addGroup(layout.createSequentialGroup()
				.addContainerGap()
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
					.addComponent(jLabel1)
					.addComponent(jButton1))
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 281, Short.MAX_VALUE)
				.addContainerGap())
		);

		getAccessibleContext().setAccessibleName("Historia Czatu");

		pack();
	}// </editor-fold>//GEN-END:initComponents

	private void jButton1MouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jButton1MouseClicked
	{//GEN-HEADEREND:event_jButton1MouseClicked
		DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
		model.setRowCount(0);
		hist.ClearFile();
	}//GEN-LAST:event_jButton1MouseClicked

		public static void main(String args[]) {
			/* Set the Nimbus look and feel */
			//<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
			/* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
			 * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
			 */
			try {
				for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
					if ("Nimbus".equals(info.getName())) {
						javax.swing.UIManager.setLookAndFeel(info.getClassName());
						break;
					}
				}
			} catch (Exception e) {
					LOG.log(Level.SEVERE, "HistoryFrame::main: ", e);
			}
			//</editor-fold>
			
				//</editor-fold>

				/* Create and display the form */
				java.awt.EventQueue.invokeLater(new Runnable() {
						@Override
						public void run() {
								new HistoryFrame().setVisible(true);
						}
				});
		}
	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton jButton1;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JScrollPane jScrollPane1;
	public javax.swing.JTable jTable1;
	// End of variables declaration//GEN-END:variables
}
