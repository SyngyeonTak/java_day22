package lecturer.day1113.xml.download;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import common.file.FileManager;

public class DownLoader extends JFrame{
	JButton bt_down;
	JProgressBar bar;
	Thread parsingThread;
	MovieHandler movieHandler;
	public DownLoader() {
		bt_down = new JButton("다운로드");
		bar = new JProgressBar();
		//스타일
		bar.setPreferredSize(new Dimension(580, 55));
		bar.setForeground(Color.CYAN);
		bar.setBackground(Color.BLACK);
		
		bar.setFont(new Font("Vernada", Font.BOLD, 25));
		bar.setStringPainted(true);
		
		setLayout(new FlowLayout());
		add(bt_down);
		add(bar);
		
		bt_down.addActionListener((e)->{
			parsingThread = new Thread() {
				public void run() {
					parseData();
					//총 몇건이 존재하는 지 출력해본다.
					int len = movieHandler.movieList.size();
					for (int i = 0; i < movieHandler.movieList.size(); i++) {
						Movie movie = movieHandler.movieList.get(i);
						download(movie.getUrl());
					}
					//반복문이 모두 수행된 이후 시점이 바로, 다운로드가 모두 완료된 시점!!
					JOptionPane.showMessageDialog(DownLoader.this, "총 "+len+"개의 파일을 다운로드 완료!!");
				}
			};
			parsingThread.start();
		});//다운로드 버튼과 리스너 연결
		
		setSize(600, 200);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
	}
	
	public void parseData() {
		//xml을 파싱하여 url만 추출해야한다!!
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			SAXParser saxParser = factory.newSAXParser();//파서객체 생성
			URL url = this.getClass().getClassLoader().getResource("res/marvel.xml");
			URI uri = url.toURI();
			File file = new File(uri);
			saxParser.parse(file, movieHandler = new MovieHandler());//파싱 시작!!!
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//인터넷상의 자원과 연결한 후 스트림으로 데이터를 읽어와 로컬 하드 경로에 저장하기!!
	public void download(String path) {//매개변수로 가져올 자원을 지정한다!!
		InputStream is = null;
		FileOutputStream fos = null;//다운받은 파일을 저장할 스트림
		int total = 0;//다운로드 받을 다원의 총 바이트 수
		int readCount = 0;//현재까지 읽은 바이트 수
		int percent = 0;
		bar.setValue(0);
		try {
			URL url = new URL(path);
			URLConnection con = url.openConnection();
			HttpURLConnection http = (HttpURLConnection)con;//웹에 특화된 connection 객체
																						//따라서 get/post 등 웹 기반 요청 가능
			http.setRequestMethod("GET");
			
			//커넥션 객체를 이용하면, 대상 자원의 크기까지 얻을 수 있다!!
			total = con.getContentLength();//연결된 자원의 바이트 반환
			
			is = http.getInputStream();//연결된 URL로 부터 입력스트림 얻기
			long time = System.currentTimeMillis();//파일명으로 사용하자.
			String ext = FileManager.getExtend(path);
			String filename = time + "." +ext;
			fos = new FileOutputStream("C:/study/ETC/academy/workspace/java_workspace/SeProject/res/download/"+filename);
			
			int data = -1;
			while(true) {
				data = is.read();
				bar.setValue((int)getPercent(readCount, total));//int형을 인수로 넣어야 하므로, 형변환하자
				System.out.println((int)getPercent(readCount, total));
				if(data == -1) break;
				readCount++;
				fos.write(data);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				if(is != null) is.close();
				if(fos != null) fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	//퍼센트 구하는 메서드 정의
	public float getPercent(int read, float total) {
		//읽은 수/ 총바이트 * 100
		
		return (read/total)*100;//소수점이 반환될 수 있으므로...
	}
	
	public static void main(String[] args) {
		new DownLoader();
	}
}


















