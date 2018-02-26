package interfaz;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import controlador.Fachada;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.ActionEvent;

public class Identificacion extends JFrame {

	private JPanel contentPane;
	private JPanel panel;
	private JPanel panel_1;
	private JButton btnAceptar;
	private JLabel lblUsuario;
	private JTextField textUsuario;
	private JLabel lblPassword;
	private JPasswordField passwordField;
	private String puerto;
	private String serverAddress;

	/**
	 * Create the frame.
	 */
	public Identificacion(String pPuerto , String pServidor) {
		setResizable(false);// Deshabilitar boton maximizar
		setTitle("Identificaci\u00F3n");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 452, 124);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		contentPane.add(getPanel(), BorderLayout.SOUTH);
		contentPane.add(getPanel_1(), BorderLayout.EAST);
		puerto=pPuerto;
		serverAddress=pServidor;
	}

	/**
	 * inicializacion del panel 1
	 * @return
	 */
	private JPanel getPanel() {
		if (panel == null) {
			panel = new JPanel();
			panel.add(getBtnAceptar());
		}
		return panel;
	}
	
	/**
	 * inicializacion del panel 2
	 * @return
	 */
	private JPanel getPanel_1() {
		if (panel_1 == null) {
			panel_1 = new JPanel();
			GroupLayout gl_panel_1 = new GroupLayout(panel_1);
			gl_panel_1.setHorizontalGroup(
				gl_panel_1.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_panel_1.createSequentialGroup()
						.addGap(49)
						.addComponent(getLblUsuario())
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(getTextUsuario(), GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
						.addGap(18)
						.addComponent(getLblPassword())
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addComponent(getPasswordField(), GroupLayout.PREFERRED_SIZE, 103, GroupLayout.PREFERRED_SIZE)
						.addGap(52))
			);
			gl_panel_1.setVerticalGroup(
				gl_panel_1.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_panel_1.createSequentialGroup()
						.addGap(5)
						.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
							.addComponent(getLblUsuario())
							.addComponent(getTextUsuario(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(getLblPassword())
							.addComponent(getPasswordField(), GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addContainerGap(17, Short.MAX_VALUE))
			);
			panel_1.setLayout(gl_panel_1);
		}
		return panel_1;
	}
	
	/**
	 * inicializacion del boton aceptar
	 * @return
	 */
	private JButton getBtnAceptar() {
		if (btnAceptar == null) {
			btnAceptar = new JButton("Aceptar");
			btnAceptar.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					String usuario = textUsuario.getText();
					char[] arrayPass = passwordField.getPassword(); 
					String pass = new String(arrayPass); 
					Fachada.getInstancia().OpenConnection(serverAddress, puerto, usuario, pass);
					Cliente.getCliente().setVisible(true);;
					dispose();
				}
			});
		}
		return btnAceptar;
	}
	
	/**
	 * inicializacion de la etiqueta de usuario
	 * @return
	 */
	private JLabel getLblUsuario() {
		if (lblUsuario == null) {
			lblUsuario = new JLabel("Usuario:");
		}
		return lblUsuario;
	}
	
	/**
	 * inicializacion del input de suario
	 * @return
	 */
	private JTextField getTextUsuario() {
		if (textUsuario == null) {
			textUsuario = new JTextField();
			textUsuario.setColumns(10);
		}
		return textUsuario;
	}
	
	/**
	 * inicializacion de la etiqueta de contraseña
	 * @return
	 */
	private JLabel getLblPassword() {
		if (lblPassword == null) {
			lblPassword = new JLabel("Password:");
		}
		return lblPassword;
	}
	
	/**
	 * inicializacion del input de contraseña
	 * @return
	 */
	private JPasswordField getPasswordField() {
		if (passwordField == null) {
			passwordField = new JPasswordField();
			passwordField.setToolTipText("Contrase�a");
		}
		return passwordField;
	}
	
}
