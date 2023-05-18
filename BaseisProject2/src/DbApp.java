import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class DbApp {
	private Connection conn;
	
	public DbApp() {
		try {
			Class.forName("org.postgresql.Driver");
			System.out.println("Driver Found!");
		} catch (ClassNotFoundException e) {
			System.out.println("Driver not found!");
		}
	}
	
	public boolean dbConnect(String ip, String dbName, String username, String password) {
		try {
			conn = DriverManager.getConnection("jdbc:postgresql://"+ip+":5432/" + dbName, username, password);
			if(conn != null) {
			System.out.println("Connection established succesfully: " + conn);

			return true;
			}
			
		} catch (SQLException e) {
			System.out.println("Failed to connect to database");
			return false;
		}
		return false;
		
	}
	
	public void dbDisconnect() {
		try {
			conn.close();
			if(conn == null) {
				System.out.println("Connection terminated succesfully");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	public String findStudent(String am){
		String amka = null;
		try {
			Statement st = conn.createStatement();
			
			ResultSet res = st.executeQuery("select amka from \"Student\" where \"Student\".am = '" + am + "' limit 1");
			if (!res.isBeforeFirst() ) {    
			    System.out.println("Student with am: " + am + " not found");
			} 
			res.next();
 			amka = res.getString(1);
			res.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		//System.out.println(amka);
		return amka;
		
	}
	
	public boolean printStudentScores(String amka, String course_code) {
		try {
			Statement st = conn.createStatement();
			
			ResultSet res = st.executeQuery("select * from \"Register\" where course_code = '" + course_code + "' and amka = '" + amka + "'");
			if (!res.isBeforeFirst() ) {    
			    return false;
			} 
			while (res.next()) {
				System.out.println("Course:" + res.getString(3)+ " Lab Grade: " + res.getInt(6) + " Exam Grade: " + res.getInt(4) + " Final Grade:" + res.getInt(5) +
						" Status: " + res.getString(7));
			}
			
			res.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;	
	}
	
	
	public boolean findCourse(String course_code) {
		try {
			PreparedStatement st = conn.prepareStatement("SELECT * FROM \"CourseRun\" WHERE course_code = ? limit 1;");
			//System.out.println(course_code);
			st.setString(1,course_code);
			ResultSet res = st.executeQuery();

			if (!res.isBeforeFirst() ) {    
				//System.out.println("1");
			    return false;
			} 
			while (res.next()) {
				//System.out.println("Course code:" + res.getString(1)+ " Serial number:"+res.getInt(2)+" Exam min:"+ res.getInt(3) + 
						//" Lab min:" + res.getInt(4)+ " Exam percentage:"+res.getInt(5)+" Lab uses:"+ res.getInt(6) +" Semester:"+ res.getInt(7));
			}
			
			res.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
		
	}
	
	
	public boolean findCourse(String course_code, int serial) {
		try {
			PreparedStatement st = conn.prepareStatement("SELECT * FROM \"CourseRun\" WHERE course_code = ? and serial_number = ?;");
			//System.out.println(course_code);
			st.setString(1,course_code);
			st.setInt(2, serial);
			ResultSet res = st.executeQuery();

			if (!res.isBeforeFirst() ) {    
				//System.out.println("1");
			    return false;
			} 
			while (res.next()) {
				//System.out.println("Course code:" + res.getString(1)+ " Serial number:"+res.getInt(2)+" Exam min:"+ res.getInt(3) + 
						//" Lab min:" + res.getInt(4)+ " Exam percentage:"+res.getInt(5)+" Lab uses:"+ res.getInt(6) +" Semester:"+ res.getInt(7));
			}
			
			res.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
		
	}

	public boolean updateStudentScores(String amka, String course_code, int serial, int new_grade) {
		try {
			PreparedStatement st = conn.prepareStatement("update \"Register\" \n"
					+ "set final_grade = ? \n"
					+ "where course_code = ? and serial_number = ? and amka = ? ");
			//System.out.println(course_code);
			st.setInt(1, new_grade);
			st.setString(2,course_code);
			st.setInt(3, serial);
			st.setString(4, amka);
			int rowsUpdated = st.executeUpdate();
			if (rowsUpdated == 0 ) {
				return false;
			}
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		printStudentScores(amka, course_code);
		return true;
	}

	
	
	@SuppressWarnings("null")
	public ArrayList<String[]> findPerson(String lname) {
		ArrayList<String[]> list = new ArrayList<>();
		try {
			PreparedStatement pst = conn.prepareStatement("SELECT * FROM public.\"Person\"\n"
					+ "WHERE\n"
					+ "surname  LIKE ?;");
			
			pst.setString(1, lname + "%");
			
			
			ResultSet rs = pst.executeQuery();
			if (!rs.isBeforeFirst() ) {    
			    return list;
			} 
			// insert everything to array and then each array to the list (hash)
			while (rs.next()) {
				String[] person =  new String[5];
				for (int i = 0; i < 5; i++) {
					person[i] = rs.getString(i+1);
				}
				list.add(person);
			}
			rs.close();
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public ArrayList<Object[]> getGrades(String amka) {
		ArrayList<Object[]> list = new ArrayList<>();
		try {
			PreparedStatement pst = conn.prepareStatement("select r.course_code, r.final_grade, r.exam_grade, r.lab_grade, c.semesterrunsin from public.\"Register\" r join \"CourseRun\" c on r.course_code = c.course_code and r.serial_number = c.serial_number\n"
					+ " where amka = ?\n"
					+ "order by semesterrunsin asc;");
			
			pst.setString(1, amka);
			
			
			ResultSet rs = pst.executeQuery();
			if (!rs.isBeforeFirst() ) {    
			    return list;
			} 
			// insert everything to array and then each array to the list (hash)
			while (rs.next()) {
				Object[] grades =  new Object[5];
				grades[0] = rs.getString(1);
				grades[1] = rs.getInt(2);
				grades[2] = rs.getInt(3);
				grades[3] = rs.getInt(4);
				grades[4] = rs.getInt(5);
				
				list.add(grades);
			}
			rs.close();
			pst.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
		
	}
	
}
	
