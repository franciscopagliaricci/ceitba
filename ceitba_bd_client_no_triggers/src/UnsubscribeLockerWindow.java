import java.awt.FlowLayout;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;


public class UnsubscribeLockerWindow extends JFrame implements ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TextField legacyField;
	private CeitbaConnection connection;
	
	/** Constructor to setup the GUI */
	public UnsubscribeLockerWindow(CeitbaConnection con) {
		this.connection = con;
		setLayout(new FlowLayout());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		// "this" Frame sets layout to FlowLayout, which arranges the components
		//  from left-to-right, and flow to next row from top-to-bottom.

		Label legacyLabel = new Label("Legajo (Obligatorio): "); 
		add(legacyLabel);               
		this.legacyField = new TextField(30);
		add(legacyField);                
		legacyField.addActionListener(this);

		setSize(290, 500);  // "this" Frame sets initial window size
		setVisible(true);   // "this" Frame shows
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		setVisible(false);
		Integer legacy = 0;
		try{
			legacy = Integer.parseInt(legacyField.getText()); 
		}catch(Exception e){
			new ErrorWindow("Legajo invalido");
			return;
		}

		String[] queryStrings = new String[2];
		queryStrings[0] = "DELETE FROM subscriptions WHERE user_id = ( SELECT id FROM users WHERE legacy = " + legacy + ") AND service_id = (SELECT id FROM services WHERE name = 'locker');";
		queryStrings[1] = "UPDATE lockers SET owner_id = NULL WHERE owner_id = ( SELECT id FROM users WHERE legacy = " + legacy + ");";
		connection.executeInsertQuerys(queryStrings);
	};
}