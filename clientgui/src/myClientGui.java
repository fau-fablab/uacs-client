import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

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

	private boolean running;

	// final static GpioController gpio = GpioFactory.getInstance();

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	private void threadInit() {
		// taken from
		// http://wiki.eclipse.org/FAQ_Why_do_I_get_an_invalid_thread_access_exception%3F

		new Thread(new Runnable() {
			public void run() {
				while (running == true) {
					try {
						Thread.sleep(500);
					} catch (Exception e) {
					}
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							doStuff();
						}
					});
				}
			}
		}).start();
	}
	
	private String convertStreamToString(java.io.InputStream is) {
		java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}

	private boolean giveFreischaltung(currentuser betreuer) throws IOException {
		if (!betreuer.getBetreuer()) {
			MessageBox myMessageBox = new MessageBox(shlIfreischaltung,
					SWT.ICON_INFORMATION | SWT.OK);
			myMessageBox.setMessage("Du bist kein Betreuer");
			myMessageBox.open();
			return false;
		}
		MessageBox myMessageBox = new MessageBox(shlIfreischaltung,
				SWT.ICON_INFORMATION | SWT.OK);
		myMessageBox.setMessage("Put the other card on the reader");
		myMessageBox.open();


		try {
			//CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpClient httpclient = HttpClientBuilder.create().build();
			HttpPost httppost = new HttpPost("http://ws01:8000/create/");

			// Request parameters and other properties.
			List<NameValuePair> params = new ArrayList<NameValuePair>(2);
			params.add(new BasicNameValuePair("requestingid", betreuer.getCardId()));
			params.add(new BasicNameValuePair("requestedid", myuser.getCardId()));
			params.add(new BasicNameValuePair("device", client.getDevice()));
			HttpResponse response;
			InputStream instream = null;
			httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				instream = entity.getContent();
				String mystring = convertStreamToString(instream);
				System.out.println(mystring);
				if (mystring.equals("done")) {
					return true;
				}
			}
			if (instream != null) {
				instream.close();
			}

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return false;
	}

	public static void main(String[] args) {
		try {
			Runtime.getRuntime().exec("sudo gpio mode 0 out");
			Runtime.getRuntime().exec("sudo gpio write 0 0");
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
		running = true;
		shlIfreischaltung.open();
		shlIfreischaltung.layout();
		client = new myclient("Laser");
		if (client.getError() == true) {
			MessageBox myMessageBox = new MessageBox(shlIfreischaltung,
					SWT.ICON_INFORMATION | SWT.OK);
			myMessageBox
					.setMessage("Your machine does not exist (yet). Closing.");
			myMessageBox.open();
			System.exit(0);
		}
		Devicename.setText(client.getDevice());
		threadInit();
		while (!shlIfreischaltung.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		running = false;
		// System.out.println("Number of Threads: " + Thread.activeCount());
		// System.out.println("i am being closed");
	}

	/**
	 * Create contents of the window.
	 */
	public void doStuff() {
		if (client != null) {
			if (!client.getActive()) {
				MessageBox myMessageBox = new MessageBox(shlIfreischaltung,
						SWT.ICON_INFORMATION | SWT.OK);
				myMessageBox
						.setMessage("Machine is inactive. Talk to a Betreuer");
				myMessageBox.open();
				System.exit(0);
			}
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
					try {
						Runtime.getRuntime().exec("sudo gpio write 0 1");
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				} else {
					EinweisungBox.setText("nicht eingewiesen");
					EinweisungBox.setBackground(myred);
					try {
						Runtime.getRuntime().exec("sudo gpio write 0 0");
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			} else {
				EinweisungBox.setBackground(myred);
				BetreuerBox.setBackground(myred);
				EinweisungBox.setText("nicht eingewiesen");
				BetreuerBox.setText("");
				try {
					Runtime.getRuntime().exec("sudo gpio write 0 0");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		} else {
			MessageBox myMessageBox = new MessageBox(shlIfreischaltung,
					SWT.ICON_INFORMATION | SWT.OK);
			myMessageBox.setMessage("Select your machine first!");
			myMessageBox.open();
			// see
			// http://www.programcreek.com/java-api-examples/org.eclipse.swt.widgets.MessageBox
		}
	}

	protected void createContents() {
		shlIfreischaltung = new Shell(); // ~SWT.RESIZE) : renders window size
											// fixed, but breaks our host window
		shlIfreischaltung.setSize(450, 300);
		shlIfreischaltung.setText("iMZS");

		Devicename = new Text(shlIfreischaltung, SWT.BORDER);
		Devicename.setEditable(false);
		Devicename.setBounds(243, 31, 181, 21);

		Button btnSetMachine = new Button(shlIfreischaltung, SWT.NONE);
		btnSetMachine.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

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
				boolean worked;
				try {
					worked = giveFreischaltung(myuser);
					MessageBox myMessageBox = new MessageBox(shlIfreischaltung,
							SWT.ICON_INFORMATION | SWT.OK);
					if (worked) {
						myMessageBox.setMessage("success");
					} else {
						myMessageBox.setMessage("did not work :(");
					}
					myMessageBox.open();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
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
