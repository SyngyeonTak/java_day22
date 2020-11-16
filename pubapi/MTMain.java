package practice.day1116.pubapi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import day1116.pubapi.MountainModel;

public class MTMain extends JFrame{
	JPanel p_west, p_center;
	JTextField t_search,  t_op1, t_op2, t_op3;
	JButton bt;
	JTable table;
	JScrollPane scroll;
	
	Vector mountainList = new Vector();
	Vector<String> columnList = new Vector<String>();
	
	MoutainModel moutainModel = new MoutainModel();
	InputStream is;
	HttpURLConnection conn;
	BufferedReader rd;
	MountainHandler handler;
	
	Thread thread;
	
	public MTMain() {
		p_west = new JPanel();
		p_center = new JPanel();
		t_search = new JTextField();
		t_op1= new JTextField();
		t_op2 = new JTextField();
		t_op3 = new JTextField();
		bt = new JButton("검색");
		table = new JTable(moutainModel = new MoutainModel());
		scroll = new JScrollPane(table);
		
		//스타일
		p_west.setPreferredSize(new Dimension(170, 600));
		p_west.setBackground(Color.WHITE);
		t_search.setPreferredSize(new Dimension(160, 30));
		t_op1.setPreferredSize(new Dimension(160, 30));
		t_op2.setPreferredSize(new Dimension(160, 30));
		t_op3.setPreferredSize(new Dimension(160, 30));
		
		//조립
		p_west.add(t_search);
		p_west.add(t_op1);
		p_west.add(t_op2);
		p_west.add(t_op3);
		p_west.add(bt);

		p_center.add(scroll);
		
		add(p_west, BorderLayout.WEST);
		add(p_center);
		
		setSize(700, 600);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
	
		bt.addActionListener((e)->{
			thread = new Thread() {
				public void run() {
					loadXML();
				}
			};
			thread.start();
		});
	}
	
	public void loadXML() {
		//apiKey 값
    	String apiKey = "FSAG8RewPKRbbF%2FAGPxvFqCPQ%2B4%2F%2BpiAjsVI9kPh%2F2eMwjIg1bmhfVp7AXHAyw1QU08g0je%2FcWC8D1GXKPZsRQ%3D%3D";
 
    	
        try {
			StringBuilder urlBuilder = new StringBuilder(" 	http://openapi.forest.go.kr/openapi/service/trailInfoService/getforeststoryservice"); /*URL*/
			urlBuilder.append("?" + URLEncoder.encode("ServiceKey","UTF-8") + "="+apiKey); /*Service Key*/
			urlBuilder.append("&" + URLEncoder.encode("mntnNm","UTF-8") + "=" + URLEncoder.encode(t_search.getText(), "UTF-8")); /**/
			urlBuilder.append("&" + URLEncoder.encode("mntnHght","UTF-8") + "=" + URLEncoder.encode("", "UTF-8")); /**/
			urlBuilder.append("&" + URLEncoder.encode("mntnAdd","UTF-8") + "=" + URLEncoder.encode("", "UTF-8")); /**/
			urlBuilder.append("&" + URLEncoder.encode("mntnInfoAraCd","UTF-8") + "=" + URLEncoder.encode("", "UTF-8")); /**/
			urlBuilder.append("&" + URLEncoder.encode("mntnInfoSsnCd","UTF-8") + "=" + URLEncoder.encode("", "UTF-8")); /**/
			urlBuilder.append("&" + URLEncoder.encode("mntnInfoThmCd","UTF-8") + "=" + URLEncoder.encode("", "UTF-8")); /**/
			URL url = new URL(urlBuilder.toString());
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-type", "application/json");
			System.out.println("Response code: " + conn.getResponseCode());
			if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
			    rd = new BufferedReader(new InputStreamReader(is = conn.getInputStream()));
			} else {
			    rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
			}
			StringBuilder sb = new StringBuilder();
			String line;
//			while ((line = rd.readLine()) != null) {
//			    sb.append(line);
//			}
			
			parseData();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void parseData() {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			SAXParser parser = factory.newSAXParser();
			parser.parse(is, handler = new MountainHandler());
			
			moutainModel.data = handler.mountainList;
			table.updateUI();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				if(rd != null) rd.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(conn != null) conn.disconnect();
		}
		
		
	}
	
	
	public static void main(String[] args) {
		new MTMain();
	}
}
























