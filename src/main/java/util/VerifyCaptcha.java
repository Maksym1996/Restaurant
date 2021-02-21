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

import consts.Captcha;
import consts.Comment;

/**
 * The class for verifying reCaptcha from Google
 */
public class VerifyCaptcha {
	public static final String SITE_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

	private static final Logger log = LogManager.getLogger(VerifyCaptcha.class);

	public static boolean verify(String gRecaptchaResponse, HttpServletRequest request) {
		log.info(Comment.BEGIN);
		if (gRecaptchaResponse == null || gRecaptchaResponse.length() == 0) {
			log.debug("gRecaptchaResponse is emptu or null");
			log.info(Comment.RETURN + false);
			return false;
		}

		try {
			URL verifyUrl = new URL(SITE_VERIFY_URL);

			HttpsURLConnection conn = (HttpsURLConnection) verifyUrl.openConnection();
			log.debug("Connection to URL");

			conn.setRequestMethod("POST");
			conn.setRequestProperty("User-Agent", request.getHeader("User-Agent"));
			conn.setRequestProperty("Accept-Language", request.getHeader("Accept-Language"));
			log.debug("Added header info to request");

			String postParams = "secret=" + Captcha.SECRET_KEY //
					+ "&response=" + gRecaptchaResponse;
			log.debug("Data for send to server");

			conn.setDoOutput(true);

			OutputStream outStream = conn.getOutputStream();
			outStream.write(postParams.getBytes());

			outStream.flush();
			outStream.close();
			log.debug("Sended request to server");

			InputStream is = conn.getInputStream();

			JsonReader jsonReader = Json.createReader(is);
			JsonObject jsonObject = jsonReader.readObject();
			jsonReader.close();
			log.debug("Get reponse by server");

			boolean success = jsonObject.getBoolean("success");
			log.info(Comment.RETURN + success);
			return success;
		} catch (Exception e) {
			log.error(Comment.EXCEPTION + e.getMessage());
			log.info(Comment.RETURN + false);
			return false;
		}
	}

	private VerifyCaptcha() {
		throw new IllegalStateException(Comment.ILLEGAL_STATE);
	}
}
