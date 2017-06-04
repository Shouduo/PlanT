package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mysql.jdbc.Connection;

/**
 * Servlet implementation class Update
 */
@WebServlet("/Update")
public class Update extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final static long ONE_DAY_MS = 24 * 60 * 60 * 1000;
	private final static long ONE_HOUR_MS = 60 * 60 * 1000;
	
	private int currentHour = 0;
	private int[] currentDate;
	
	private String[] dailyGapDates;
	private String[] hourlyGapDates;
	private String[] hourlyGapTimes;

	/** life cycle. */
	public Update() {
		super();
	}

	public void destroy() {
		super.destroy();
	}

	// ��������ǰʱ���Сʱ��0~23��
	public int currentHour() {
		Calendar current = Calendar.getInstance();
		int hour = current.get(Calendar.HOUR_OF_DAY);
		return hour;
	}

	// ��������ǰ���ڣ�int[]{3, 9}��
	public int[] currentDate() {
		Calendar current = Calendar.getInstance();
		int month = current.get(Calendar.MONTH);
		int day = current.get(Calendar.DAY_OF_MONTH);
		return new int[] { month + 1, day };
	}

	// ��Сʱ��ʽ��Ϊ12:00����ʽ��00:00~23:00��
	public String formatTime(int i) {
		String str = null;
		if (i < 0 || i > 23) {
			str = "fuckup";
		} else {
			if (i < 10) {
				str = "0" + i + ":00";
			} else {
				str = i + ":00";
			}
		}
		return str;
	}

	// �����ڸ�ʽ��Ϊ3/9����ʽ
	public String formatDate(int[] i) {
		String str = null;
		if (i[0] < 0 || i[0] > 12) {
			str = "fuckup";
		} else if (i[1] < 0 || i[1] > 31) {
			str = "fuckup";
		} else {
			str = i[0] + "/" + i[1];
		}
		return str;
	}

	
	//Daily
	// �������ݿ�������һ�������뼴�����µ����ݵ��������
	public int calDateGap() {
		Connection conn = null;
		PreparedStatement pstate = null;
		ResultSet rs = null;
		String date = null;
		String lastRowRetrieve = "select date from " + DBUtil.TABLE_DAILY_DATA + " order by id desc limit 1";
		try {
			conn = DBUtil.getConnect();
			pstate = conn.prepareStatement(lastRowRetrieve);
			rs = pstate.executeQuery();
			if (rs.next()) {
				date = rs.getString("date");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstate != null)
					pstate.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		String[] dateSet = date.split("/");
		int[] lastDate = new int[] { Integer.parseInt(dateSet[0]), Integer.parseInt(dateSet[1]) };

		int days = 0;
		do {
			Calendar startCalendar = Calendar.getInstance();
			startCalendar.set(days == 0 ? startCalendar.get(Calendar.YEAR) : startCalendar.get(Calendar.YEAR) - 1,
					lastDate[0] - 1, lastDate[1], 0, 0, 0);
			Calendar endCalendar = Calendar.getInstance();
			endCalendar.set(endCalendar.get(Calendar.YEAR), currentDate[0] - 1, currentDate[1], 0, 0, 0);
			days = (int) (((endCalendar.getTimeInMillis() - startCalendar.getTimeInMillis())) / ONE_DAY_MS);
		} while (days < 0);
		days = days > 14 ? 14 : days;

		int i = days;
		dailyGapDates = new String[days];
		for (; i > 0; i--) {
			Calendar currentCalendar = Calendar.getInstance();
			currentCalendar.set(currentCalendar.get(Calendar.YEAR), currentDate[0] - 1, currentDate[1], 0, 0, 0);
			long gapTimeInMills = currentCalendar.getTimeInMillis() - ONE_DAY_MS * (i - 1);
			Date gapDate = new Date(gapTimeInMills);
			Calendar gapCalendar = Calendar.getInstance();
			gapCalendar.setTime(gapDate);
			int month = gapCalendar.get(Calendar.MONTH) + 1;
			int day = gapCalendar.get(Calendar.DAY_OF_MONTH);
			dailyGapDates[(days - i)] = formatDate(new int[] { month, day });
		}
		return days;
	}

	// �������ڼ�������ɾ����
	public void rewindDailyData() {
		int days = calDateGap();
		Connection conn = null;
		PreparedStatement pstate = null;
		int rs = 0;
		String dataRowInsert = "insert into " + DBUtil.TABLE_DAILY_DATA
				+ " (date, consume, bright, tempDiff) values (?, 0, 0, 0)";
		String dataRowDelete = "delete from " + DBUtil.TABLE_DAILY_DATA + " order by id asc limit 1";
		for (int i = 0; i < days; i++) {
			try {
				conn = DBUtil.getConnect();
				pstate = conn.prepareStatement(dataRowInsert);
				pstate.setString(1, dailyGapDates[i]);
				rs = pstate.executeUpdate();
				if (rs == 1) {
					//
				}

				pstate.close();
				pstate = conn.prepareStatement(dataRowDelete);
				rs = pstate.executeUpdate();
				if (rs == 1) {
					//
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					if (rs != 0)
						rs = 0;
					if (pstate != null)
						pstate.close();
					if (conn != null)
						conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	
	//Hourly
	// �������ݿ�������һСʱ�����뼴�����µ����ݵ�Сʱ�����
	public int calTimeGap() {
		Connection conn = null;
		PreparedStatement pstate = null;
		ResultSet rs = null;
		String date = null;
		String time = null;
		String lastRowRetrieve = "select * from " + DBUtil.TABLE_HOURLY_DATA + " order by id desc limit 1";
		try {
			conn = DBUtil.getConnect();
			pstate = conn.prepareStatement(lastRowRetrieve);
			rs = pstate.executeQuery();
			if (rs.next()) {
				date = rs.getString("date");
				time = rs.getString("time");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstate != null)
					pstate.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		String[] dateSet = date.split("/");
		int[] lastDate = new int[] { Integer.parseInt(dateSet[0]), Integer.parseInt(dateSet[1]) };
		int lastHour = Integer.parseInt(time.substring(0, 2));

		int hours = 0;
		do {
			Calendar startCalendar = Calendar.getInstance();
			startCalendar.set(hours == 0 ? startCalendar.get(Calendar.YEAR) : startCalendar.get(Calendar.YEAR) - 1,
					lastDate[0] - 1, lastDate[1], lastHour, 0, 0);
			Calendar endCalendar = Calendar.getInstance();
			endCalendar.set(endCalendar.get(Calendar.YEAR), currentDate[0] - 1, currentDate[1], currentHour, 0, 0);
			hours = (int) (((endCalendar.getTimeInMillis() - startCalendar.getTimeInMillis())) / ONE_HOUR_MS);
		} while (hours < 0);
		hours = hours > 24 ? 24 : hours;

		int i = hours;
		hourlyGapDates = new String[hours];
		hourlyGapTimes = new String[hours];
		for (; i > 0; i--) {
			Calendar currentCalendar = Calendar.getInstance();
			currentCalendar.set(currentCalendar.get(Calendar.YEAR), currentDate[0] - 1, currentDate[1], currentHour, 0,
					0);
			long gapTimeInMills = currentCalendar.getTimeInMillis() - ONE_HOUR_MS * (i - 1);
			Date gapDate = new Date(gapTimeInMills);
			Calendar gapCalendar = Calendar.getInstance();
			gapCalendar.setTime(gapDate);
			int month = gapCalendar.get(Calendar.MONTH) + 1;
			int day = gapCalendar.get(Calendar.DAY_OF_MONTH);
			int hour = gapCalendar.get(Calendar.HOUR_OF_DAY);
			hourlyGapDates[(hours - i)] = formatDate(new int[] { month, day });
			hourlyGapTimes[(hours - i)] = formatTime(hour);
		}
		return hours;
	}

	// ����Сʱ����������ɾ����
	public void rewindHourlyData() {
		int hours = calTimeGap();
		Connection conn = null;
		PreparedStatement pstate = null;
		int rs = 0;
		String dataRowInsert = "insert into " + DBUtil.TABLE_HOURLY_DATA
				+ " (date, time, hum, consume, bright, temp) values (?, ?, 0, 0, 0, 0)";
		String dataRowDelete = "delete from " + DBUtil.TABLE_HOURLY_DATA + " order by id asc limit 1";
		for (int i = 0; i < hours; i++) {
			try {
				conn = DBUtil.getConnect();
				pstate = conn.prepareStatement(dataRowInsert);
				pstate.setString(1, hourlyGapDates[i]);
				pstate.setString(2, hourlyGapTimes[i]);
				rs = pstate.executeUpdate();
				if (rs == 1) {
					//
				}

				pstate.close();
				pstate = conn.prepareStatement(dataRowDelete);
				rs = pstate.executeUpdate();
				if (rs == 1) {
					//
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					if (rs != 0)
						rs = 0;
					if (pstate != null)
						pstate.close();
					if (conn != null)
						conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	
	
	/** deprecated. */
	
	// ���������µ�ʱ�������е�ԭ��id
	public int idOfUpdateData() {
		Connection conn = null;
		PreparedStatement pstate = null;
		ResultSet rs = null;
		int id = -1;
		String dataRowRetrieve = "select id from " + DBUtil.TABLE_HOURLY_DATA + " where time = ?";
		try {
			conn = DBUtil.getConnect();
			pstate = conn.prepareStatement(dataRowRetrieve);
			pstate.setString(1, formatTime(currentHour));
			rs = pstate.executeQuery();
			if (rs.next()) {
				id = rs.getInt("id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstate != null)
					pstate.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return id;
	}

	// ����ʱ��ڵ㸳ֵÿ��id
	public void rewindHourlyData1() {
		Connection conn = null;
		PreparedStatement pstate = null;
		int rs = 0;
		String dataRowSetId = "update " + DBUtil.TABLE_HOURLY_DATA + " set id = ? where time = ?";

		for (int i = 23; i > -1; i--) {
			int actualTime = (currentHour + (24 - i)) % 24;
			try {
				conn = DBUtil.getConnect();
				pstate = conn.prepareStatement(dataRowSetId);
				pstate.setInt(1, i);
				pstate.setString(2, formatTime(actualTime));
				rs = pstate.executeUpdate();
				if (rs == 1) {

				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					if (rs != 0)
						rs = 0;
					if (pstate != null)
						pstate.close();
					if (conn != null)
						conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// ����Hourly���ݿ�
	public void updateHourlyData() {
		int affectId = idOfUpdateData();
		int offset = 23 - affectId;

		Connection conn = null;
		PreparedStatement pstate = null;
		int rs = 0;
		String dataRowReset = "update " + DBUtil.TABLE_HOURLY_DATA
				+ " set hum = 0, consume = 0, bright = 0, temp = 0 where id = ?";

		for (; offset > 0; offset--) {
			try {
				conn = DBUtil.getConnect();
				pstate = conn.prepareStatement(dataRowReset);
				pstate.setInt(1, affectId + offset);
				rs = pstate.executeUpdate();
				if (rs == 1) {

				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					if (rs != 0)
						rs = 0;
					if (pstate != null)
						pstate.close();
					if (conn != null)
						conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		rewindHourlyData1();
	}
	
	/** deprecated. */
	
	
	// �����һʱ�̵�hum
	public int getLastHum() {
		Connection conn = null;
		PreparedStatement pstate = null;
		ResultSet rs = null;
		int lastHum = 0;
		String lastHumRetrieve = "select hum from " + DBUtil.TABLE_HOURLY_DATA
				+ " where time = ?";
		try {
			conn = DBUtil.getConnect();
			pstate = conn.prepareStatement(lastHumRetrieve);
			pstate.setString(1, formatTime((currentHour - 1 + 24) % 24));
			rs = pstate.executeQuery();
			if (rs.next()) {
				lastHum = rs.getInt("hum");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstate != null)
					pstate.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return lastHum;
	}
	
	// �����һʱ�̵�temp
	public int getLastTemp() {
		Connection conn = null;
		PreparedStatement pstate = null;
		ResultSet rs = null;
		int lastTemp = 0;
		String lastTempRetrieve = "select temp from " + DBUtil.TABLE_HOURLY_DATA
				+ " where time = ?";
		try {
			conn = DBUtil.getConnect();
			pstate = conn.prepareStatement(lastTempRetrieve);
			pstate.setString(1, formatTime((currentHour - 1 + 24) % 24));
			rs = pstate.executeQuery();
			if (rs.next()) {
				lastTemp = rs.getInt("temp");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstate != null)
					pstate.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return lastTemp;
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		currentHour = currentHour();
		currentDate = currentDate();
		PrintWriter out = response.getWriter();
		
		int hum = 0;
		int consume = 0;
		int bright = 0;
		int temp = 0;
		if (request.getParameter("hum") != null && request.getParameter("bright") != null
				&& request.getParameter("temp") != null) {

			rewindHourlyData();
			rewindDailyData();

			hum = Integer.parseInt(request.getParameter("hum"));
			consume = (getLastHum() - hum) > 0 ? getLastHum() - hum : 0;
			bright = Integer.parseInt(request.getParameter("bright"));
			temp = Integer.parseInt(request.getParameter("temp"));
			
			if (temp == 85) {	//���DS18B20����Ϊ�˱�֤�¶����ݱ仯ƽ������ʹ����һʱ�̴��桮85��
				temp = getLastTemp();
			}

			Connection conn = null;
			PreparedStatement pstate = null;
			int rs = 0;
			ResultSet rs1 = null;
			String hourlyDataRowUpdate = "update " + DBUtil.TABLE_HOURLY_DATA
					+ " set hum = ?, consume = ?, bright = ?, temp = ? where date = ? and time = ?";

			String dailyDateRowRetrieve = "select * from " + DBUtil.TABLE_HOURLY_DATA
					+ " where date = ?";
			String dailyDataRowUpdate = "update " + DBUtil.TABLE_DAILY_DATA
					+ " set consume = ?, bright = ?, tempDiff = ? where date = ?";
			try {
				conn = DBUtil.getConnect();
				pstate = conn.prepareStatement(hourlyDataRowUpdate);
				pstate.setInt(1, hum);
				pstate.setInt(2, consume);
				pstate.setInt(3, bright);
				pstate.setInt(4, temp);
				pstate.setString(5, formatDate(currentDate));
				pstate.setString(6, formatTime(currentHour));
				rs = pstate.executeUpdate();

				int consumeDaily = 0;
				int brightDaily = 0;
				int maxTemp = -50;
				int minTemp = 50;
				pstate.close();
				pstate = conn.prepareStatement(dailyDateRowRetrieve);
				pstate.setString(1, formatDate(currentDate));
				rs1 = pstate.executeQuery();
				while (rs1.next()) {
					consumeDaily = consumeDaily + rs1.getInt("consume");
					brightDaily = brightDaily + rs1.getInt("bright");
					maxTemp = rs1.getInt("temp") > maxTemp ? rs1.getInt("temp") : maxTemp;
					minTemp = rs1.getInt("temp") < minTemp ? rs1.getInt("temp") : minTemp;
				}

				pstate.close();
				pstate = conn.prepareStatement(dailyDataRowUpdate);
				pstate.setInt(1, consumeDaily);
				pstate.setInt(2, brightDaily);
				pstate.setInt(3, maxTemp - minTemp);
				pstate.setString(4, formatDate(currentDate));
				rs = pstate.executeUpdate();

				if (rs == 1) {
					out.print("ok");
				} else if (rs == 0) {
					out.print("no good");
				} else {
					out.print("fuck up");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					if (rs != 0)
						rs = 0;
					if (rs1 != null)
						rs1.close();
					if (pstate != null)
						pstate.close();
					if (conn != null)
						conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
