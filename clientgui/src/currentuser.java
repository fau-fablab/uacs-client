

import java.io.IOException;
import java.io.InputStream;
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
	
	private String convertStreamToString(java.io.InputStream is) {
		java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}
	
	private void checkForEinweisung(String fauid, String Geraet) {
		this.error = false;
		try {
			// TODO: change the way i read the json to https://docs.oracle.com/javase/tutorial/networking/urls/readingURL.html
			
			// or: http://stackoverflow.com/questions/9856195/how-to-read-an-http-input-stream
			
			String urlstring = "http://localhost:8000/users/";
			urlstring += fauid;
			URL myurl = new URL(urlstring);
			InputStream is = myurl.openStream();
			// see
			// http://stackoverflow.com/questions/309424/read-convert-an-inputstream-to-a-string
			String myInputString = convertStreamToString(is);
			// System.out.println(myInputString);
			JSONParser parser = new JSONParser();
			Object myObj = parser.parse(myInputString);
			
			JSONArray myJsonObj = (JSONArray) myObj;
			JSONObject myParsedJsonObj = (JSONObject)((JSONObject) myJsonObj.get(0)).get("fields");

			// http://examples.javacodegeeks.com/core-java/json/java-json-parser-example/
			
			if ((boolean) myParsedJsonObj.get(Geraet)) {
				this.einweisung = true;
			}
			if ((boolean) myParsedJsonObj.get("Betreuer")) {
				this.betreuer = true;
			}
			this.fauid = myParsedJsonObj.get("fauid").toString();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Either the fauid " + fauid + " is unknown or there is a problem with your network.");
			this.error = true;
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (java.lang.NullPointerException e) {
			System.out.println("Theese arent the droids you're looking for. Check with your local Django to add "
					+ Geraet + " to the list of supported devices.");
			this.error = true;
		}
	}
	
	public currentuser(String fauid, String myGeraet) {
		this.checkForEinweisung(fauid, myGeraet);
	}
}
