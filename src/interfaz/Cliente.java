package interfaz;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import net.miginfocom.swing.MigLayout;

import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

public class Cliente extends JFrame implements Observer{

	private JPanel contentPane;
	private JPanel panel;
	private JPanel panel_1;
	private JPanel panel_2;
	private JTextArea txtrSql;
	private JTextField txtServerAddress;
	private JTextField txtPort;
	private JButton btnLogin;
	private JButton btnLogout;
	private JButton botonExecute;
	private JButton botonQuery;
	private JTextArea txtrInformationArea;
	private JTextArea txtrNotificationArea;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Cliente frame = new Cliente();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Cliente() {
		setTitle("Cliente");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 470, 477);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		contentPane.add(getPanel(), BorderLayout.NORTH);
		contentPane.add(getPanel_1(), BorderLayout.SOUTH);
		contentPane.add(getPanel_2(), BorderLayout.CENTER);
		modelo.GestorBD.getGestorBD().addObserver(this);
	}

	private JPanel getPanel() {
		if (panel == null) {
			panel = new JPanel();
			panel.add(getTxtServerAddress());
			panel.add(getTxtPort());
			panel.add(getBtnLogin());
			panel.add(getBtnLogout());
		}
		return panel;
	}
	private JPanel getPanel_1() {
		if (panel_1 == null) {
			panel_1 = new JPanel();
		}
		return panel_1;
	}
	private JPanel getPanel_2() {
		if (panel_2 == null) {
			panel_2 = new JPanel();
			GroupLayout gl_panel_2 = new GroupLayout(panel_2);
			gl_panel_2.setHorizontalGroup(
				gl_panel_2.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_panel_2.createSequentialGroup()
						.addGroup(gl_panel_2.createParallelGroup(Alignment.LEADING)
							.addGroup(gl_panel_2.createSequentialGroup()
								.addContainerGap()
								.addComponent(getTxtrSql(), GroupLayout.PREFERRED_SIZE, 319, GroupLayout.PREFERRED_SIZE)
								.addGap(18)
								.addGroup(gl_panel_2.createParallelGroup(Alignment.LEADING)
									.addComponent(getBotonExecute())
									.addComponent(getBotonQuery(), GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
							.addGroup(gl_panel_2.createSequentialGroup()
								.addGap(74)
								.addComponent(getTxtrNotificationArea(), GroupLayout.PREFERRED_SIZE, 288, GroupLayout.PREFERRED_SIZE))
							.addGroup(gl_panel_2.createSequentialGroup()
								.addContainerGap()
								.addComponent(getTxtrInformationArea(), GroupLayout.DEFAULT_SIZE, 424, Short.MAX_VALUE)))
						.addGap(26))
			);
			gl_panel_2.setVerticalGroup(
				gl_panel_2.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_panel_2.createSequentialGroup()
						.addGroup(gl_panel_2.createParallelGroup(Alignment.TRAILING)
							.addGroup(Alignment.LEADING, gl_panel_2.createSequentialGroup()
								.addContainerGap()
								.addComponent(getTxtrSql(), GroupLayout.PREFERRED_SIZE, 94, GroupLayout.PREFERRED_SIZE))
							.addGroup(Alignment.LEADING, gl_panel_2.createSequentialGroup()
								.addGap(29)
								.addComponent(getBotonQuery())
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(getBotonExecute())))
						.addGap(18)
						.addComponent(getTxtrInformationArea(), GroupLayout.PREFERRED_SIZE, 126, GroupLayout.PREFERRED_SIZE)
						.addGap(16)
						.addComponent(getTxtrNotificationArea(), GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)
						.addContainerGap())
			);
			panel_2.setLayout(gl_panel_2);
		}
		return panel_2;
	}
	private JTextArea getTxtrSql() {
		if (txtrSql == null) {
			txtrSql = new JTextArea();
			txtrSql.setText("SQL sentence");
		}
		return txtrSql;
	}
	private JTextField getTxtServerAddress() {
		if (txtServerAddress == null) {
			txtServerAddress = new JTextField();
			txtServerAddress.setText("Server address");
			txtServerAddress.setColumns(10);
		}
		return txtServerAddress;
	}
	private JTextField getTxtPort() {
		if (txtPort == null) {
			txtPort = new JTextField();
			txtPort.setText("Port");
			txtPort.setColumns(10);
		}
		return txtPort;
	}
	private JButton getBtnLogin() {
		if (btnLogin == null) {
			btnLogin = new JButton("Login");
			btnLogin.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Identificacion ident = new Identificacion();
					ident.setVisible(true);
					String res= modelo.GestorBD.getGestorBD().OpenConnection(txtServerAddress.getText(),txtPort.getText());
				}
			});
		}
		return btnLogin;
	}
	private JButton getBtnLogout() {
		if (btnLogout == null) {
			btnLogout = new JButton("Logout");
			btnLogout.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String res= modelo.GestorBD.getGestorBD().CloseConnection();
					txtrNotificationArea.setText(res);
				}
			});
		}
		return btnLogout;
	}
	private JButton getBotonExecute() {
		if (botonExecute == null) {
			botonExecute = new JButton("Execute");
			botonExecute.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String update = txtrSql.getText();
					String res = modelo.GestorBD.getGestorBD().Update(update);
					txtrNotificationArea.setText(res);
/////////////Falta que cuente las lias afectadas y muestre el numero por area de notificaciones
				}
			});
		}
		return botonExecute;
	}
	private JButton getBotonQuery() {
		if (botonQuery == null) {
			botonQuery = new JButton("Query");
			botonQuery.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String query = txtrSql.getText();
					ResultSet resultado = modelo.GestorBD.getGestorBD().Select(query);
					try {
						txtrNotificationArea.setText("Numero de tuplas: "+resultado.getFetchSize());
					} catch (SQLException error) {
						// TODO Auto-generated catch block
						error.printStackTrace();
						txtrNotificationArea.setText(error.getMessage());
					}
				}
			});
		}
		return botonQuery;
	}
	private JTextArea getTxtrInformationArea() {
		if (txtrInformationArea == null) {
			txtrInformationArea = new JTextArea();
			txtrInformationArea.setText("Information area");
		}
		return txtrInformationArea;
	}
	private JTextArea getTxtrNotificationArea() {
		if (txtrNotificationArea == null) {
			txtrNotificationArea = new JTextArea();
			txtrNotificationArea.setText("Notification area");
		}
		return txtrNotificationArea;
	}
	
	//muestra las notificaciones en la interfaz
	private void mostrarNotificacion(String notificacion){
		txtrNotificationArea.setText(notificacion);
	}

	@Override
	public void update(Observable o, Object arg) {
		if (arg.equals("update")) {
			mostrarNotificacion("Base de Datos modificada.");
		} 
		if (arg.equals("select")) {
			mostrarNotificacion("Visualizando la informacion requerida de la Base de Datos.");
		} 
		if (arg.equals("error_update")) {
			mostrarNotificacion("Error al modificar la Base de Datos.");
		} 
		if (arg.equals("error_select")) {
			mostrarNotificacion("Error al visualizar el contenido de la Base de Datos.");
		}
	}
	
}

