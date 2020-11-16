package practice.day1116.pubapi;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

public class MoutainModel extends AbstractTableModel{

	Vector<Mountain> data = new Vector<Mountain>();
	Vector<String> columnName = new Vector<String>();
	
	public MoutainModel() {
		columnName.add("ID");
		columnName.add("산 이름");
		columnName.add("산 소재지");
		columnName.add("산 높이");
	}
	
	public int getRowCount() {
		return data.size();
	}

	public int getColumnCount() {
		return columnName.size();
	}

	public String getColumnName(int col) {
		return columnName.get(col);
	}
	
	public Object getValueAt(int row, int col) {
		String obj = null;
		Mountain mt = data.get(row);
		
		if(col == 0) {
			obj = Integer.toString(mt.getMntnid());
		}else if(col == 1) {
			obj = mt.getMntnnm();
		}else if(col == 2) {
			obj = mt.getMntninfopoflc();
		}else if(col == 3) {
			obj = Integer.toString(mt.getMntninfohght());
		}
		return obj;
	}
}
