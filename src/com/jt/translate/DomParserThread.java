package com.jt.translate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
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
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import com.sun.org.apache.xalan.internal.xsltc.compiler.sym;

public class DomParserThread extends Thread {

	static DocumentBuilderFactory mDocumentBuilderFactory = DocumentBuilderFactory
			.newInstance();
	DocumentBuilder mDocumentBuilder;
	private final String PARSER_FILE_NAME = "strings.xml";
	private final String BASE_FILE_NAME = "values";

	private String mToLanguage = "in";
	private String mFromLanguage = "en";
	private int mLength;

	int mIndex;

	public DomParserThread(String from, String to) {
		// TODO Auto-generated constructor stub#
		mFromLanguage = from;
		mToLanguage = to;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();

		DomParserHandle();
	}

	public void DomParserHandle() {
		Document document = null;
		Document outDocument = null;

		try {
			mDocumentBuilder = mDocumentBuilderFactory.newDocumentBuilder();
			document = mDocumentBuilder.parse(new File(PARSER_FILE_NAME));

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Element rootElement = document.getDocumentElement();

		outDocument = mDocumentBuilder.newDocument();
		Element outRootElement = outDocument.createElement(rootElement
				.getTagName());
		outDocument.appendChild(outRootElement);

		// NodeList nodeList = rootElement.getChildNodes();
		NodeList nodeList = document.getElementsByTagName("string");

		int length = nodeList.getLength();
		println("length:" + length);
		
		File path = new File(BASE_FILE_NAME + "-" + mToLanguage);
		if (!path.exists())
			path.mkdir();
		
		for (int i = 0; i < length; i++) {
			Element e = (Element) nodeList.item(i);
			
			String text = e.getTextContent();
			println("index: " + (length - i) + "--" + "text: " + text);
			
			Element outE = outDocument.createElement(e.getTagName());
			NamedNodeMap map = e.getAttributes();
			int attrLen = map.getLength();
			for (int n = 0; n < attrLen; n++) {
				Node node = map.item(n);
				String name = node.getNodeName();
				String value = node.getNodeValue();
				outE.setAttribute(name, value);
				outRootElement.appendChild(outE);
			}

			String traslateText = "";
			final String sStr = "%s";
			if (text.contains(sStr)) {
				String [] t = text.split("[%]s");
				boolean flag = false;
				int len = t.length;
				int total = (text.length() - text.replaceAll(sStr, "").length())/sStr.length();
				// println("total = " + total);
				for (int n = 0; n < len; n++) {
					// println("t[" + n + "] = "+ t[n]);
					traslateText += myTranslate(t[n], mFromLanguage, mToLanguage);
					if (n < total) {
						traslateText += " %s ";
					}
				}
			} else {
				traslateText += myTranslate(text, mFromLanguage, mToLanguage);
			}

			println("index: " + (length - i) + "--" + "traslateText: " + traslateText);


			Text outText = outDocument.createTextNode(traslateText);
			outE.appendChild(outText);
			
			writeXML(outDocument, BASE_FILE_NAME + "-" + mToLanguage + "/"
					+ PARSER_FILE_NAME);
		}

		// System.out.println(rootElement.getTagName());
	}

	public void println(String str) {
		System.out.println(mToLanguage + "-->"
				+ Thread.currentThread().getName() + "-->" + str);
	}

	public void writeXML(Document document, String filename) {
		// synchronized (mDocumentBuilderFactory) {
		try {
			document.normalize();
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			// 编码
			DOMSource source = new DOMSource(document);
			PrintWriter pw = new PrintWriter(new FileOutputStream(filename));
			StreamResult result = new StreamResult(pw);
			transformer.transform(source, result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// }
	}

	private HttpPost httppost;

	private HttpResponse response;

	private final DefaultHttpClient httpclient = new DefaultHttpClient();

	/*
	 * 
	 * @param text 翻译原文
	 * 
	 * 
	 * 
	 * @param lanFrom 源语言
	 * 
	 * 
	 * 
	 * @param lanTo 目标语言
	 * 
	 * 
	 * 
	 * @return 翻译后的字符串
	 */

	public String myTranslate(String text, String lanFrom, String lanTo) {

		List<NameValuePair> qparams = new ArrayList<NameValuePair>();

		qparams.add(new BasicNameValuePair("hl", "en"));

		qparams.add(new BasicNameValuePair("ie", "UTF-8"));

		qparams.add(new BasicNameValuePair("sl", lanFrom));

		qparams.add(new BasicNameValuePair("text", text));

		qparams.add(new BasicNameValuePair("tl", lanTo));

		// HttpClient提交post请求

		httppost = new HttpPost("http://translate.google.cn/translate_t#");

		// String cookie =

		// "Cookie PREF=ID=8daa1f767f10d1fe:U=f5ac701cf7d3f2e0:FF=0:LD=en:CR=2:TM=1277174286:LM=1289370601:S=q7yslRWEZs3uK1H8; NID=39=UO-TWo9HzzjHc-d_wYm7BVR1cH33KpqaN5h5877_i29nERA93FeG1GSuV3ZSvsOx8D-TnHKpB9m0KhZRH8U9uPwoE-arYd0bAyAlILyXZxLO2_TyGQhJpcMiOLVEuCpq; SID=DQAAAHoAAADMlGzeKhnGkbkIJ36tVO0ZPXgmQ6Cth7Oa6geyyE1WJooW8P01uKUHNrsRkjggvFMAWIWB9J5i18z0F6GjC_oV79mSwXEDGuRFGhRnDyJdid3ptjFW0pIyt4_2D6AMIqtOWF71aWdvY7IvAU1AWMNs8fBZHAOgRqtf3aCUkr36ZA; HSID=A6-YJTnhjBdFWukoR";

		// httppost.addHeader("Cookie", cookie);

		String responseBody = "";

		String content = "";

		try {

			// 将参数封装到post数据包中

			httppost.setEntity(new UrlEncodedFormEntity(qparams, HTTP.UTF_8));

			response = httpclient.execute(httppost);

			responseBody = EntityUtils.toString(response.getEntity());

			// 过滤出所需翻译后的内容

			// responseBody = getHTMLWithString(responseBody);
			
			// responseBody = HTMLDecoder.decode(responseBody);
			
			int tmp1 = responseBody.indexOf("result_box");
			int tmp2 = responseBody.indexOf(">", tmp1);
			int tmp3 = responseBody.indexOf("</div>", tmp2);

			// 替换换行符和其他网页标签
			// content = responseBody = HTMLDecoder.decode(responseBody);

			content = responseBody.substring(tmp2 + 1, tmp3)
			.replaceAll("<br>", "\n").replaceAll("<[^>]*>", "");
			
			content = HTMLDecoder.decode(content);
			// content = responseBody;
			// content = getHTMLWithString(content);

		} catch (Exception e) {

			return content;

		} finally {

			httppost.abort();

		}

		// content = getHTMLWithString(content);
		return content;

	}
	
	private static void print(String msg, Object... args) {
	    try {
	        PrintStream ps = new PrintStream(System.out, true, "UTF-8");
	        ps.println(String.format(msg, args));
	    } catch (UnsupportedEncodingException error) {
	        System.err.println(error);
	        System.exit(0);
	    }
	}


	/*
	 * 
	 * 重载上面的translate()方法，实现文本数组的翻译
	 */

	public String[] myTranslate(String[] text, String lanFrom,

	String lanTo) {

		if (text == null || text.length < 1) {

			return null;

		}

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < text.length; i++) {

			if (i < text.length - 1) {

				sb.append(text[i]).append("BBaaBB");

			} else {

				sb.append(text[i]);

			}

		}

		return myTranslate(sb.toString(), lanFrom, lanTo).split("BBaaBB");

	}

	public String getHTMLWithString(String contentC) {
		String content = contentC;
		if (content == null) {
			return "";
		}
		content = content.replaceAll("&amp;", "&");
		content = content.replaceAll("&lt;", "<");
		content = content.replaceAll("&gt;", ">");
		content = content.replaceAll("&quot;", "\"");
		content = content.replaceAll("\r&#10;", "　\n");
		content = content.replaceAll("&#10;", "　\n");
		content = content.replaceAll("&#032;", " ");
		content = content.replaceAll("&#039;", "'");
		content = content.replaceAll("&#033;", "!");

		return content;
	}

	public static String getStringWithHTML(String contentC) {
		String content = contentC;
		if (content == null) {
			return "";
		}
		content = content.replaceAll("&", "&amp;");
		content = content.replaceAll("<", "&lt;");
		content = content.replaceAll(">", "&gt;");
		content = content.replaceAll("\"", "&quot;");
		content = content.replaceAll("\n\r", "&#10;");
		content = content.replaceAll("\r\n", "&#10;");
		content = content.replaceAll("\n", "&#10;");
		content = content.replaceAll(" ", "&#032;");
		content = content.replaceAll("'", "&#039;");
		content = content.replaceAll("!", "&#033;");

		return content;
	}

}
