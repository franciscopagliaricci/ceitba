import java.awt.FlowLayout;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;


public class NewServiceWindow extends JFrame implements ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TextField nameField;
	private TextField durationField;
	private TextField descriptionField;
	private TextField priceField;
	private CeitbaConnection connection;
	
	/** Constructor to setup the GUI */
	public NewServiceWindow(CeitbaConnection con) {
		this.connection = con;
		setLayout(new FlowLayout());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		// "this" Frame sets layout to FlowLayout, which arranges the components
		//  from left-to-right, and flow to next row from top-to-bottom.

		Label nameLabel = new Label("Nombre (Obligatorio): "); // construct Label
		add(nameLabel);               // "this" Frame adds Label
		this.nameField = new TextField(30); // construct TextField
		add(nameField);                // "this" Frame adds TextField
		nameField.addActionListener(this);

		Label descriptionLabel = new Label("Descripcion: "); 
		add(descriptionLabel);               
		this.descriptionField = new TextField(30);
		add(descriptionField);                
		descriptionField.addActionListener(this);

		Label priceLabel = new Label("Precio (Obligatorio): "); 
		add(priceLabel);               
		this.priceField = new TextField(30);
		add(priceField);                
		priceField.addActionListener(this);

		Label durationLabel = new Label("Duracion: (en meses)"); 
		add(durationLabel);               
		this.durationField = new TextField(30);
		add(durationField);                
		durationField.addActionListener(this);

		setSize(300, 400);  // "this" Frame sets initial window size
		setVisible(true);   // "this" Frame shows
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		setVisible(false);
		
		Double price = 0.0;
		try{
			price = Double.parseDouble(priceField.getText()); 
		}catch(Exception e){
			new ErrorWindow("Precio invalido");
			return;
		}
		
		String duration = "";
		if (durationField.getText().length() > 0){
			try{
				duration = "" + Integer.parseInt(durationField.getText()); 
			}catch(Exception e){
				new ErrorWindow("Duracion invalida");
				return;
			}
		}
		
		String name = nameField.getText().toLowerCase();
		String description = descriptionField.getText().toLowerCase();

		if ( ! name.matches("[a-zA-z0-9\\s]+") ){
			new ErrorWindow("Nombre invalido: solo puede contener letras y numeros");
			return;			
		}
						
		if ( ! description.matches("[a-zA-z0-9\\s]*") ){
			new ErrorWindow("Descripcion invalida: solo puede contener letras y numeros");
			return;			
		}
		if (description.length() == 0){
			description = "NULL";
		}else{
			description = "'" + description + "'";
		}

		String queryString1 = "INSERT INTO services(name, price) VALUES ('"+ name + "', "+  price + ");";
		SqlQuery query1 = new SqlInsertQuery(queryString1, null, connection);
		query1.run();

		String queryString = "INSERT INTO other_services(id, name, description, duration) VALUES ((SELECT id FROM services WHERE name = '" + name + "'), '"+ name + "', " + description + ", " + duration + ");";
		if (duration.length() == 0){
			queryString = "INSERT INTO other_services(id, name, description) VALUES ((SELECT id FROM services WHERE name = '" + name + "'), '"+ name + "', " + description + ");";
		}
		SqlQuery query = new SqlInsertQuery(queryString, null, connection);
		query.run();
		
	};
}
