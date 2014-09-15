/**
 * 
 */
package com.bottinifuel.pladd.EFT;

import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 * @author pladd
 *
 */
public class EFTChecker extends JFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField ABAnum;

	/**
	 * @throws HeadlessException
	 */
	public EFTChecker() throws HeadlessException {
		super("EFT Checker");
		
		BankLookupInitializer i = new BankLookupInitializer();
		i.start();
		
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        Box inputbox = new Box(BoxLayout.X_AXIS);

        JLabel ABALabel  = new JLabel("ABA(Routing) #: ");
        inputbox.add(ABALabel);
        
        ABAnum = new JTextField();
        ABAnum.setColumns(20);
        ABAnum.addActionListener(this);
        ABAnum.setActionCommand("run");
        inputbox.add(ABAnum);

        JButton CheckButton  = new JButton("Check it!");
        inputbox.add(CheckButton);

        CheckButton.setMnemonic(KeyEvent.VK_C);
        CheckButton.setDisplayedMnemonicIndex(0);
        CheckButton.setActionCommand("run");
        CheckButton.addActionListener(this);

        this.getContentPane().add(inputbox);
        this.pack();
        //this.show();
        this.setVisible(true);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new EFTChecker();
	}

	public void actionPerformed(ActionEvent e) {
		String event = e.getActionCommand();
		
		if (event.equals("run"))
        {
            try
            {
            	String abastr = ABAnum.getText().trim();
            	char [] aba = new char[abastr.length()];
            	aba = abastr.toCharArray();
            	EFTRoutingNumber rn = new EFTRoutingNumber(aba);
            		String bank = BankLookup.BankName(rn);
            		if (bank == null)
            			bank = "Unknown";
            		JOptionPane.showMessageDialog(this,
                        "Valid ABA Number\n"+"Bank: "+bank,
            			"Valid ABA",
                        JOptionPane.INFORMATION_MESSAGE);
            }
            catch (NumberFormatException error)
            {
        		JOptionPane.showMessageDialog(this,
                        "Invalid - " + error.getMessage(),
        				"Invalid ABA", 
                        JOptionPane.ERROR_MESSAGE);
            }
            
            return;
        }
	}

	private class BankLookupInitializer extends Thread
	{
		public BankLookupInitializer()
		{
			super("BankLookupInitializer");
		}
		public void run()
		{
			BankLookup.BankName(null);
		}
	}
}
