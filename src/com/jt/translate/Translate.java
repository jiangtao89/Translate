package com.jt.translate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

public class Translate {

	private static final String PARSER_FILE_NAME = "strings.xml";
	private static final String BASE_FILE_NAME = "values";

	static DocumentBuilderFactory mDocumentBuilderFactory = DocumentBuilderFactory
			.newInstance();
	static DocumentBuilder mDocumentBuilder;

	private static String mToLanguage = "in";
	private static String mFromLanguage = "en";
	private static ArrayList<String> mToLanguages = new ArrayList<String>();
	private static ArrayList<Document> mDocuments = new ArrayList<Document>();
	private static ArrayList<Element> mRootElement = new ArrayList<Element>();

	// "en",
	// "fr",
	// "id",
	// "hi",
	// "fa",
	// "de",
	//
	// };

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Translate translate = new Translate();

		System.out.println("args: " + args);
		System.out.println("args length: " + args.length);
		int strLength = args.length;

		for (String str : args) {
			System.out.println("str: " + str);
		}

		if (strLength == 0) {
			System.out.println("Wrong input format!");
			System.exit(0);
		} else if (strLength == 1) {
			if (args[0].equals("-help") || args[0].equals("-h")) {
				System.out.print("-lan en fr id");
			} else {
				System.out.println("Wrong input format!");
				System.exit(0);
			}
		} else {
			// mFromLanguage = args[0];
			for (int i = 1; i < strLength; i++) {
				// mToLanguages.add(args[i]);
				new DomParserThread(args[0], args[i]).start();
			}
		}

		// translate.DomParserHandle();

		System.out.println("Translate");
	}

}
