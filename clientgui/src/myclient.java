import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class myclient {

	private String Geraet;
	private int runtime;
	private boolean active;
	private boolean error;

	public String getDevice() {
		return this.Geraet;
	}

	public boolean getError() {
		return this.error;
	}

	public boolean getActive() {
		return this.active;
	}

	public int getRuntime() {
		return this.runtime;
	}

	public myclient(String name) {
		this.Geraet = name;
		this.getDeviceData();
	}

	private String convertStreamToString(java.io.InputStream is) {
		java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}

	private void getDeviceData() {
		try {
			String urlstring = "http://ws01:8000/devices/";
			urlstring += this.getDevice();
			// System.out.println(urlstring);
			URL myurl = new URL(urlstring);
			InputStream is = myurl.openStream();
			// see
			// http://stackoverflow.com/questions/309424/read-convert-an-inputstream-to-a-string
			String myInputString = convertStreamToString(is);
			// System.out.println(myInputString);
			JSONParser parser = new JSONParser();
			Object myObj = parser.parse(myInputString);

			JSONArray myJsonObj = (JSONArray) myObj;
			JSONObject myParsedJsonObj = (JSONObject) ((JSONObject) myJsonObj
					.get(0)).get("fields");

			// http://examples.javacodegeeks.com/core-java/json/java-json-parser-example/
			String mystring = this.getDevice();
			String djangostring = myParsedJsonObj.get("Name").toString();
			// System.out.println(mystring + " " + djangostring);
			if (mystring.matches(djangostring) == false) {
				System.out.println("problem with device");
				this.runtime = 0;
				this.active = false;
				this.error = true;
			}
			this.runtime = Integer.parseInt(myParsedJsonObj.get("runtime")
					.toString());
			this.active = Boolean.parseBoolean(myParsedJsonObj.get("active")
					.toString());

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// System.out.println("Either the device " + this.Geraet +
			// " is unknown or there is a problem with your network.");
			this.error = true;
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (java.lang.NullPointerException e) {
			System.out
					.println("Theese arent the droids you're looking for. Check with your local Django to add "
							+ Geraet
							+ " to the list of supported devices. You might also have typos in your specified fields");
			this.error = true;
		}
	}

}
