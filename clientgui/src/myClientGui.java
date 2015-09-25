import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.graphics.Color;

import org.eclipse.swt.widgets.MessageBox; // this probably wont be needed in the final version

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class myClientGui {

	protected Shell shlIfreischaltung;
	private Text Devicename;
	private myclient client;
	private currentuser myuser;
	private Text ID;
	private Text BetreuerBox;
	private Text EinweisungBox;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			myClientGui window = new myClientGui();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shlIfreischaltung.open();
		shlIfreischaltung.layout();
		while (!shlIfreischaltung.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shlIfreischaltung = new Shell(); // ~SWT.RESIZE) : renders window size fixed, but breaks our host window
		shlIfreischaltung.setSize(450, 300);
		shlIfreischaltung.setText("iMZS");

		Devicename = new Text(shlIfreischaltung, SWT.BORDER);
		Devicename.setEditable(false);
		Devicename.setBounds(243, 31, 181, 21);

		Button btnSetMachine = new Button(shlIfreischaltung, SWT.NONE);
		btnSetMachine.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				client = new myclient("Laser");
				Devicename.setText(client.getDevice());
			}
		});
		btnSetMachine.setBounds(27, 31, 190, 64);
		btnSetMachine.setText("setMachine");

		ID = new Text(shlIfreischaltung, SWT.BORDER);
		ID.setEditable(false);
		ID.setBounds(243, 74, 181, 21);

		Button btnGetpermissions = new Button(shlIfreischaltung, SWT.NONE);
		btnGetpermissions.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (client != null) {
					myuser = new currentuser("13917", client.getDevice());
					Color myred = new Color(null, 255, 0, 0);
					Color mygreen = new Color(null, 0, 240, 0);
					if (!myuser.getError()) {
						ID.setText(myuser.getFauId());
						if (myuser.getBetreuer()) {
							BetreuerBox.setText("Betreuer");
							BetreuerBox.setBackground(mygreen);
						} else {
							BetreuerBox.setText("kein Betreuer");
							BetreuerBox.setBackground(myred);
						}
						if (myuser.getEinweisung()) {
							EinweisungBox.setText("Eingewiesen");
							EinweisungBox.setBackground(mygreen);
						} else {
							EinweisungBox.setText("nicht eingewiesen");
							EinweisungBox.setBackground(myred);
						}
					} else {
						EinweisungBox.setBackground(myred);
						BetreuerBox.setBackground(myred);
						EinweisungBox.setText("nicht eingewiesen");
						BetreuerBox.setText("");
					}
				} else {
					MessageBox myMessageBox = new MessageBox(shlIfreischaltung, SWT.ICON_INFORMATION | SWT.OK);
					myMessageBox.setMessage("Select your machine first!");
					myMessageBox.open();
					// see
					// http://www.programcreek.com/java-api-examples/org.eclipse.swt.widgets.MessageBox
				}
			}
		});
		btnGetpermissions.setBounds(27, 142, 190, 65);
		btnGetpermissions.setText("getPermissions");

		BetreuerBox = new Text(shlIfreischaltung, SWT.BORDER);
		BetreuerBox.setEditable(false);
		BetreuerBox.setBounds(243, 142, 181, 21);

		EinweisungBox = new Text(shlIfreischaltung, SWT.BORDER);
		EinweisungBox.setEditable(false);
		EinweisungBox.setBounds(243, 186, 181, 21);

	}
}
