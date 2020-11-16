package practice.day1116.pubapi;

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class MountainHandler extends DefaultHandler{
	Vector<Mountain> mountainList;
	Mountain mt;
	
	boolean isMntnid;
	boolean isMntnnm;
	boolean isMntninfopoflc;
	boolean isMntninfohght;
	
	public void startDocument() throws SAXException {
		
	}

	public void startElement(String uri, String localName, String tag, Attributes attributes) throws SAXException {
		if(tag.equals("items")) {
			mountainList = new Vector<Mountain>();
		}else if(tag.equals("item")) {
			mt = new Mountain();
		}else if(tag.equals("mntnid")) {
			isMntnid = true;
		}else if(tag.equals("mntnnm")) {
			isMntnnm = true;
		}else if(tag.equals("mntninfopoflc")) {
			isMntninfopoflc = true;
		}else if(tag.equals("mntninfohght")) {
			isMntninfohght = true;
		}
	}
	
	public void characters(char[] ch, int start, int length) throws SAXException {
		String data = new String(ch, start, length);
		
		if(isMntnid) {
			mt.setMntnid(Integer.parseInt(data));
		}else if(isMntnnm) {
			mt.setMntnnm(data);
		}else if(isMntninfopoflc) {
			mt.setMntninfopoflc(data);
		}else if(isMntninfohght) {
			mt.setMntninfohght(Integer.parseInt(data));
		}
		
	};
	
	public void endElement(String uri, String localName, String tag) throws SAXException {
		if(tag.equals("item")) {
			mountainList.add(mt);
		}else if(tag.equals("mntnid")) {
			isMntnid = false;
		}else if(tag.equals("mntnnm")) {
			isMntnnm = false;
		}else if(tag.equals("mntninfopoflc")) {
			isMntninfopoflc = false;
		}else if(tag.equals("mntninfohght")) {
			isMntninfohght = false;
		}
	}
	
	public void endDocument() throws SAXException {
		for (int i = 0; i < mountainList.size(); i++) {
			System.out.println("산 id "+mt.getMntnid());
			System.out.println("산 이름 "+mt.getMntnnm());
			System.out.println("산 소재지 "+mt.getMntninfopoflc());
			System.out.println("산 높이 "+mt.getMntninfohght());
			System.out.println("-------------------------------------------------");
		}
	}
}
















