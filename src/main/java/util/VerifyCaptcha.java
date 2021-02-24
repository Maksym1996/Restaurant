package util;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import consts.CaptchaConst;
import consts.Log;

/**
 * The class for verifying reCaptcha from Google
 */
public class VerifyCaptcha {
	public static final String SITE_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

	private static final Logger LOG = LogManager.getLogger(VerifyCaptcha.class);

	public static boolean verify(String gRecaptchaResponse, HttpServletRequest request) {
		LOG.info(Log.BEGIN);
		if (gRecaptchaResponse == null || gRecaptchaResponse.length() == 0) {
			LOG.debug("gRecaptchaResponse is emptu or null");
			LOG.info(Log.RETURN + false);
			return false;
		}

		try {
			URL verifyUrl = new URL(SITE_VERIFY_URL);

			HttpsURLConnection conn = (HttpsURLConnection) verifyUrl.openConnection();
			LOG.debug("Connection to URL");

			conn.setRequestMethod("POST");
			conn.setRequestProperty("User-Agent", request.getHeader("User-Agent"));
			conn.setRequestProperty("Accept-Language", request.getHeader("Accept-Language"));
			LOG.debug("Added header info to request");

			String postParams = "secret=" + CaptchaConst.SECRET_KEY //
					+ "&response=" + gRecaptchaResponse;
			LOG.debug("Data for send to server");

			conn.setDoOutput(true);

			OutputStream outStream = conn.getOutputStream();
			outStream.write(postParams.getBytes());

			outStream.flush();
			outStream.close();
			LOG.debug("Sended request to server");

			InputStream is = conn.getInputStream();

			JsonReader jsonReader = Json.createReader(is);
			JsonObject jsonObject = jsonReader.readObject();
			jsonReader.close();
			LOG.debug("Get reponse by server");

			boolean success = jsonObject.getBoolean("success");
			LOG.info(Log.RETURN + success);
			return success;
		} catch (Exception e) {
			LOG.error(Log.EXCEPTION + e.getMessage());
			LOG.info(Log.RETURN + false);
			return false;
		}
	}

	private VerifyCaptcha() {
		//nothing
	}
}
