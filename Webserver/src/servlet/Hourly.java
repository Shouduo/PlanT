package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mysql.jdbc.Connection;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Servlet implementation class DataServlet
 */
@WebServlet("/Hourly")
public class Hourly extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public Hourly() {
        super();
    }
    
    public void destroy() {
    	super.destroy();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		
		JSONArray array = new JSONArray();
		Connection conn = null;
		java.sql.PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "select * from " + DBUtil.TABLE_HOURLY_DATA + " order by id asc";
		
		try {
			conn = DBUtil.getConnect();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				JSONObject temp = new JSONObject()
						.element("time", rs.getString("time"))
						.element("hum", rs.getInt("hum"))
						.element("consume", rs.getInt("consume"))
						.element("bright", rs.getInt("bright"))
						.element("temp", rs.getInt("temp"));
				array.add(temp);
			}
			out.print(array.toString());
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(ps != null) ps.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
}
