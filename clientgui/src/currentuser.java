import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class currentuser {

	private boolean betreuer = false;
	private boolean einweisung = false;
	private String fauid = "";
	private boolean error;
	private String cardid;

	public boolean getBetreuer() {
		return this.betreuer;
	}

	public boolean getEinweisung() {
		return this.einweisung;
	}

	public String getFauId() {
		return this.fauid;
	}

	public boolean getError() {
		return this.error;
	}

	public String getCardId() {
		return this.cardid;
	}

	private String getcardid() {
		// partly taken from http://alvinalexander.com/java/edu/pj/pj010016

		// were running the getuid-pcsc tool from the command line to read the
		// cardid from the cardreader

		String s = null;
		String t = null;
		String retstring = "";
		String errorstring = "";

		try {
			Process p = Runtime.getRuntime().exec("getuid-pcsc");

			BufferedReader stdInput = new BufferedReader(new InputStreamReader(
					p.getInputStream()));

			BufferedReader stdError = new BufferedReader(new InputStreamReader(
					p.getErrorStream()));

			// read the output from the command
			if ((s = stdInput.readLine()) != null) {
				retstring += s;
			}
			if ((t = stdError.readLine()) != null) {
				errorstring += t;
			}

			if (retstring.contains("SCard")
					|| (errorstring.contains("APPLICATION_NOT_FOUND"))) {
				// most of the cardreaders error messages will contain the
				// string noted above.
				// the reader will return "application not found" on stderr for
				// tokens
				return "error";
			}

			// read any errors from the attempted command
			while ((s = stdError.readLine()) != null) {
				System.out
						.println("Here is the standard error of the command (if any):\n");
				System.out.println(s);
			}
			return retstring;
		}

		catch (IOException e) {
			System.out.println("exception happened - here's what I know: ");
			// e.printStackTrace();
			return ("error");
		}
	}

	private String convertStreamToString(java.io.InputStream is) {
		java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}

	private void checkForEinweisung(String fauid, String Geraet) {
		this.error = false;
		try {
			this.cardid = getcardid();
			if (this.cardid.equals("error") == false) {

				String urlstring = "http://ws01:8000/users/";
				urlstring += this.cardid;
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

				if ((boolean) myParsedJsonObj.get(Geraet)) {
					this.einweisung = true;
				}
				if ((boolean) myParsedJsonObj.get("Betreuer")) {
					this.betreuer = true;
				}
				this.fauid = myParsedJsonObj.get("fauid").toString();
			} else {
				this.einweisung = false;
				this.betreuer = false;
				this.fauid = "unknown";
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// System.out.println("Either the cardid " + this.cardid +
			// " is unknown or there is a problem with your network.");
			this.error = true;
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (java.lang.NullPointerException e) {
			System.out
					.println("Theese arent the droids you're looking for. Check with your local Django to add "
							+ Geraet + " to the list of supported devices.");
			this.error = true;
		}
	}

	public currentuser(String fauid, String myGeraet) {
		this.checkForEinweisung(fauid, myGeraet);
	}
}