import javax.swing.JFrame;

public class CooMan extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CooMan() {
		add(new Model());
	}
	
	public static void main(String[] args) {
		CooMan coo = new CooMan();
		coo.setVisible(true);
		coo.setTitle("Coo-Man");
		coo.setSize(556,610);
		coo.setDefaultCloseOperation(EXIT_ON_CLOSE);
		coo.setLocationRelativeTo(null);
	}

}
