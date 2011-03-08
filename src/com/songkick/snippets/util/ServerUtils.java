package com.songkick.snippets.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class ServerUtils {
	private static final int RETRIES = 2;

	public static void postData(InputStream inStream, URL endpoint, Writer output) throws IOException {
		HttpURLConnection urlConnection = null;
		OutputStream out = null;
		InputStream in = null;

		try {
			urlConnection = (HttpURLConnection) endpoint.openConnection();
			urlConnection.setRequestMethod("POST");
			urlConnection.setDoOutput(true);
			urlConnection.setDoInput(true);
			urlConnection.setUseCaches(false);
			urlConnection.setAllowUserInteraction(false);
			urlConnection.setRequestProperty("Content-Type", "binary/octet-stream");

			out = urlConnection.getOutputStream();
			pipe(inStream, out);
			out.close();

			if (output != null) {
				in = urlConnection.getInputStream();
				Reader reader = new InputStreamReader(in);
				pipe(reader, output);
				reader.close();
			}
		} finally {
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.close();
			}
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
		}
	}

	/**
	 * Pipes everything from the reader to the writer via a buffer
	 */
	private static void pipe(Reader reader, Writer writer) throws IOException {
		char[] buf = new char[1024];
		int read = 0;
		while ((read = reader.read(buf)) >= 0) {
			writer.write(buf, 0, read);
		}
		writer.flush();
	}

	/**
	 * Pipes everything from the reader to the writer via a buffer
	 */
	private static void pipe(InputStream inStream, OutputStream outStream)
			throws IOException {
		byte[] buf = new byte[1024];
		int amount = 0;
		int nextBuffer = 0;
		while ((nextBuffer = inStream.read(buf)) >= 0) {
			amount += nextBuffer;
			outStream.write(buf);
		}
		outStream.flush();
	}

	public static ArrayList<String> readPageLines(String urlString)
			throws MalformedURLException {
		URL url = new URL(urlString);
		int retries = 0;

		// Try to access the server up to 3 times before giving up
		while (retries < RETRIES) {
			try {
				return readPage(url);
			} catch (java.net.SocketTimeoutException e) {
				e.printStackTrace();
				// Do nothing - force a retry
			} catch (IOException ex) {
				ex.printStackTrace();
				// Do nothing - force a retry
			}
			retries++;
		}

		return null;
	}

	public static byte[] readBytes(String urlString) throws MalformedURLException {
		try {
			URL url = new URL(urlString);
			URLConnection connection = url.openConnection();
			connection.setConnectTimeout(1000);

			Reader reader = new InputStreamReader(connection.getInputStream());
			ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			Writer writer = new BufferedWriter(new OutputStreamWriter(byteOut));

			pipe(reader, writer);

			return byteOut.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String readPage(String urlString) throws MalformedURLException {
		URL url = new URL(urlString);
		System.out.println("URL=" + url);
		int retries = 0;

		// Try to access the server up to 3 times before giving up
		while (retries < 3) {
			try {
				System.out.println("Trying URL with retries=" + retries);
				ArrayList<String> lines = readPage(url);

				String result = "";
				for (String line : lines) {
					result += line + "\n";
				}

				return result;

			} catch (java.net.SocketTimeoutException e) {
				// Do nothing - force a retry
			} catch (IOException ex) {
				ex.printStackTrace();
				System.err.println("Cause=" + ex.toString());
				// Do nothing - force a retry
			}
			retries++;
		}

		return null;
	}

	private static ArrayList<String> readPage(URL url) throws IOException {
		try {
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(10000);

			String line = null;
			ArrayList<String> result = new ArrayList<String>();
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection
					.getInputStream()));
			
			System.out.println("BufferedReader=" + reader);

			while ((line = reader.readLine()) != null) {
				result.add(line);
			}

			return result;
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
	}
}
