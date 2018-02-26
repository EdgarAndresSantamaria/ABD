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
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.json.simple.JSONArray;

import controlador.Fachada;
import net.miginfocom.swing.MigLayout;

import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

public class Cliente extends JFrame {
	/**
	 * atributos de la ventana Principal de l aplicacion
	 */

	private JPanel contentPane;
	private JPanel panel;
	private JPanel panel_1;
	private JPanel panel_2;
	private JTextArea txtrSql;
	private static JTextField txtServerAddress;
	private static JTextField txtPort;
	private JButton btnLogin;
	private JButton btnLogout;
	private JButton botonExecute;
	private JButton botonQuery;
	private JTextArea txtrInformationArea;
	JTextArea txtrNotificationArea;

	private static Cliente mCliente;

	/**
	 * método estatico para recuperar la única instancia de cliente
	 * @return
	 */
	public static Cliente getCliente() {
		if (mCliente == null)
			mCliente = new Cliente();
		return mCliente;
	}

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
		setResizable(false);// Deshabilitar boton maximizar
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
	}

	/**
	 * inicializacion del panel "identificacion"
	 * @return
	 */
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

	/**
	 * inicialización del panel ""
	 * @return
	 */
	private JPanel getPanel_1() {
		if (panel_1 == null) {
			panel_1 = new JPanel();
		}
		return panel_1;
	}

	/**
	 * inicialización del panel ""
	 * @return
	 */
	private JPanel getPanel_2() {

		if (panel_2 == null) {
			panel_2 = new JPanel();
			GroupLayout gl_panel_2 = new GroupLayout(panel_2);
			gl_panel_2.setHorizontalGroup(gl_panel_2.createParallelGroup(Alignment.LEADING).addGroup(gl_panel_2
					.createSequentialGroup()
					.addGroup(gl_panel_2.createParallelGroup(Alignment.LEADING).addGroup(gl_panel_2
							.createSequentialGroup().addContainerGap()
							.addComponent(getTxtrSql(), GroupLayout.PREFERRED_SIZE, 319, GroupLayout.PREFERRED_SIZE)
							.addGap(18)
							.addGroup(gl_panel_2.createParallelGroup(Alignment.LEADING).addComponent(getBotonExecute())
									.addComponent(getBotonQuery(), GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
											Short.MAX_VALUE)))
							.addGroup(gl_panel_2.createSequentialGroup().addGap(74).addComponent(
									getTxtrNotificationArea(), GroupLayout.PREFERRED_SIZE, 288, GroupLayout.PREFERRED_SIZE))
							.addGroup(gl_panel_2.createSequentialGroup().addContainerGap().addComponent(
									getTxtrInformationArea(), GroupLayout.DEFAULT_SIZE, 424, Short.MAX_VALUE)))
					.addGap(26)));
			gl_panel_2.setVerticalGroup(gl_panel_2.createParallelGroup(Alignment.LEADING).addGroup(gl_panel_2
					.createSequentialGroup()
					.addGroup(gl_panel_2.createParallelGroup(Alignment.TRAILING)
							.addGroup(Alignment.LEADING,
									gl_panel_2.createSequentialGroup().addContainerGap().addComponent(getTxtrSql(),
											GroupLayout.PREFERRED_SIZE, 94, GroupLayout.PREFERRED_SIZE))
							.addGroup(Alignment.LEADING,
									gl_panel_2.createSequentialGroup().addGap(29).addComponent(getBotonQuery())
											.addPreferredGap(ComponentPlacement.RELATED)
											.addComponent(getBotonExecute())))
					.addGap(18)
					.addComponent(getTxtrInformationArea(), GroupLayout.PREFERRED_SIZE, 126, GroupLayout.PREFERRED_SIZE)
					.addGap(16).addComponent(getTxtrNotificationArea(), GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)
					.addContainerGap()));
			panel_2.setLayout(gl_panel_2);
		}
		return panel_2;
	}
	
	/**
	 * inicializar el bloque para introducir sentencias SQL
	 * @return
	 */
	private JTextArea getTxtrSql() {
		if (txtrSql == null) {
			txtrSql = new JTextArea();
			txtrSql.setText("SQL sentence");
		}
		return txtrSql;
	}
	
	/**
	 * creación del campo para recoger el nombre del servidor, por defecto localhost
	 * @return
	 */
	public  JTextField getTxtServerAddress() {
		if (txtServerAddress == null) {
			txtServerAddress = new JTextField();
			txtServerAddress.setText("Server Address");
			txtServerAddress.setColumns(10);
		}
		return txtServerAddress;
	}
	
	/**
	 * creación del campo para recoger el puerto del servidor , por defecto 3306
	 * @return
	 */
	public  JTextField getTxtPort() {
		if (txtPort == null) {
			txtPort = new JTextField();
			txtPort.setText("Port");
			txtPort.setColumns(10);
		}
		return txtPort;
	}
	
	
	/**
	 * inicialización del boton login
	 * @return
	 */
	private JButton getBtnLogin() {
		if (btnLogin == null) {
			btnLogin = new JButton("Login");
			btnLogin.addActionListener(new  ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// abrir la interfaz de logueo
					Identificacion ident = new Identificacion(txtPort.getText(),txtServerAddress.getText());
					ident.setVisible(true);
					dispose();

				}
			});
		}
		return btnLogin;
	}

	/**
	 * inicialización del boton logout
	 * @return
	 */
	private JButton getBtnLogout() {
		if (btnLogout == null) {
			btnLogout = new JButton("Logout");
			btnLogout.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// Desconectar de la base de datos
					Fachada.getInstancia().CloseConnection();
				}
			});
		}
		return btnLogout;
	}

	/**
	 * inicializar boton de ejecución de las sentencias SQL (Update e Insert)
	 * @return
	 */
	private JButton getBotonExecute() {
		if (botonExecute == null) {
			botonExecute = new JButton("Execute");
			botonExecute.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String update = txtrSql.getText();
					Fachada.getInstancia().Update(update);
				}
			});
		}
		return botonExecute;
	}
	
	/**
	 * inicializa el boton para lanzar una Query (Select)
	 * @return
	 */
	private JButton getBotonQuery() {
		if (botonQuery == null) {
			botonQuery = new JButton("Query");
			botonQuery.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String query = txtrSql.getText();
					JSONArray resultado = Fachada.getInstancia().Select(query);
					Iterator<Object>it=resultado.iterator();
					txtrInformationArea.append("\n");
					while(it.hasNext()) {
						Object tmp=it.next();
						System.out.println(tmp.toString());
						txtrInformationArea.append(tmp.toString()+"\n");	
					}
					setVisible(true);

				}
			});
		}
		return botonQuery;

	}

	/**
	 * inicializa el area de informacion
	 * @return
	 */
	private JTextArea getTxtrInformationArea() {
		if (txtrInformationArea == null) {
			txtrInformationArea = new JTextArea();
			txtrInformationArea.setText("Information area");
		}
		return txtrInformationArea;
	}
	

	/**
	 * inicializa el area de notificacion de errores
	 * @return
	 */
	private JTextArea getTxtrNotificationArea() {

		if (txtrNotificationArea == null) {
			txtrNotificationArea = new JTextArea();
			txtrNotificationArea.setText("Notification area");
		}
		return txtrNotificationArea;
	}
	
	/**
	 * metodo para mostrar errores en el area de notificacion de errores
	 * @param error
	 */
	public void throwException(String error) {
		//cargar error
		txtrNotificationArea.setText(error);
		setVisible(true);
	}
}
