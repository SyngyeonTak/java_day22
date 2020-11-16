/*
 * DBeaver 수준은 아니어도, 딕셔너리를 학습하기 위해 데이터베이스 접속 클라이언트를 자바로 만들어본다.
 * 실무에서는 SQLPlus를 잘 사용하지 않음 이유) 업무효율성이 떨어지기 때문...
 * 			그럼 언제쓰나? 실무 현장에서는 개발자의 pc에는 각종 개발툴들이 있지만, 실제적인 운영서버에는 
 * 			보안 상 아무것도 설치해서는 안된다. 따라서 서버에는 툴들이 없기 때문에 만일 오라클을 유지보수하러
 * 			파견을 나간 경우, 콘솔창 기반으로 쿼리를 다뤄야할 경우가 종종 있다.. 이때 SQLPlus를 써야함
 * 
 * 개발자들이 개발할 떄 데이터베이스 다루는 툴을 "데이터베이스 접속 클라이언트"라고 부른다.
 * 이러한 툴 들중 꽤 유명한 제품은 Toad, 등이 있다..(유료)
 * Toad는 DVeaver에 비해 기능이 막강하지만 유료이기에 우리는 DBeaver를 사용하고 있음
 * 
 * */

package day1116.dbclient;

import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class DBMSClientApp extends JFrame{
	JPanel p_west;//서쪽 영역 패널
	Choice ch_users; //유저명이 출력될 초이스 컴포넌트
	JPasswordField t_pass;//비밀번호 텍스트 필드
	JButton bt_login;//접속 버튼
	
	JPanel p_center;//그리드가 적용될 센터 패널
	JTable t_tables;//유저의 테이블 정보를 출력할 JTable 
	JTable t_seq;//유저의 시퀀스 정보를 출력할 JTable 
	JScrollPane s1, s2;//스크롤 2개 준비
	
	Connection con;
	String url = "jdbc:oracle:thin:@localhost:1521:XE";
	String user = "system";
	String password = "1234";
	
	//테이블을 출력할 백터 및 컬럼
	Vector tableList  = new Vector();//이 백터안에는 추후 또다른 백터가 들어갈 예정
													//단, 이차원 배열보다는 크기가 자유로워서 유연함... 코딩하기 쉬움
	Vector<String> columnList = new Vector<String>();
	
	
	
	public DBMSClientApp() {
		columnList.add("table_name");
		columnList.add("tablespace_name");
		//생성
		p_west = new JPanel();
		ch_users = new Choice();
		t_pass = new JPasswordField();
		bt_login = new JButton("접속");
		
		p_center = new JPanel();
		p_center.setLayout(new GridLayout(2, 1));//2층에 1호수
		t_tables = new JTable(tableList, columnList);
		t_seq = new JTable();
		s1 = new JScrollPane(t_tables);
		s2 = new JScrollPane(t_seq);
		
		//스타일
		p_west.setPreferredSize(new Dimension(150, 350));
		ch_users.setPreferredSize(new Dimension(145, 30));
		t_pass.setPreferredSize(new Dimension(145, 30));
		bt_login.setPreferredSize(new Dimension(145, 30));
		
		//조립
		p_west.add(ch_users);
		p_west.add(t_pass);
		p_west.add(bt_login);
		
		p_center.add(s1);
		p_center.add(s2);
		
		add(p_west, BorderLayout.WEST);
		add(p_center);
		
		setVisible(true);
		//setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				disconnect();
				System.exit(0);
			}
		});
		setSize(700, 350);
		setLocationRelativeTo(null);
		connect();
		getUserList();

		bt_login.addActionListener((e)->{
			login();
		});
		
	}
	
	//현재 접속 유저의 테이블 목록 가져오기
	public void getTableList() {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "select table_name, tablespace_name from user_tables";
		try {
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				Vector v = new Vector();//tablelList백터에 담겨질 백터
				v.add(rs.getString("table_name"));
				v.add(rs.getString("tablespace_name"));
				
				tableList.add(v);//멤버변수 백터에 담기..
				
			}
			//완성된 이차원 백터를 JTable에 반영해야 함, 생성자의 인수로 넣자
			t_tables.updateUI();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	public void login() {
		disconnect();//접속끊기
		user = ch_users.getSelectedItem();//현재 초이스 컴포넌트에 선택된 아이템 값
		password = new String(t_pass.getPassword());
		connect();
		getTableList();//바로 이 시점에 로그인하자마자, 이 사람의 테이블 정보를 보여주는 게 좋다
		System.out.println("보유한 테이블 갯수: "+tableList.size());
	}
	
	
	//유저목록 가져오기
	public void getUserList() {
		//pstmt와 result는 소모품이므로 매 쿼리문마다 1개씩 대응
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "select username from dba_users order by username asc";
		
		try {
			pstmt = con.prepareStatement(sql);//쿼리문 준비하기
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				ch_users.add(rs.getString("username"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	public void connect() {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection(url, user, password);//접속시도
			if(con == null) {
				JOptionPane.showMessageDialog(this, user+"계정의 접속에 실패하였습니다.");
			}else {
				this.setTitle(user+" 계정으로 접속 중...");//프레임 제목에 성공 출력
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public void disconnect() {
		try {
			if(con != null)con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	
	public static void main(String[] args) {
		new DBMSClientApp();
	}

}
















