import java.util.ArrayList;
import java.util.Scanner;

public class Main {
	static DbApp db = new DbApp();
	static Scanner sc = new Scanner(System.in);
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		dbconnect();
		while(1!=0) {
			printMenu();
			int i = sc.nextInt();
			if(i == 1) {
				viewGrade();
			}else if(i == 2){
				changeGrade();
			}
			else if(i == 3){
				searchPerson();
			}
			else if(i == 4) {
				viewAnalGrades();
			}
			else if(i == 5) {
				disconnect();
			}
		}
	}
	


	static void dbconnect1() {
		db.dbConnect("localhost", "Project2023", "postgres", "your_password");
	}
	
	static void disconnect() {
		db.dbDisconnect();
		System.exit(0);
	}
	
	static void dbconnect() {
		while(!connect()) {
			System.out.println("please try again");
		}
	}
	
	static boolean connect() {
		System.out.println("Connect to server:");
		System.out.println("Ip: ");
		String ip = sc.nextLine();
		System.out.println("Project name: ");
		String pname = sc.nextLine();
		System.out.println("Username: ");
		String uname = sc.nextLine();
		System.out.println("Password: ");
		String pword = sc.nextLine();
		
		if (!db.dbConnect(ip, pname, uname, pword)) {
			return false;
		};
		return true;
	}
	
	static void printMenu() {
		System.out.println("==============================================================MENU==============================================================");
		System.out.println("\t1.View Student Grade.");
		System.out.println("\t2.Change Student Grade.");
		System.out.println("\t3.Search Person.");
		System.out.println("\t4.View Analytical Grading.");
		System.out.println("\t5.Disconnect");
	}
	
	
	static void viewGrade() {
		sc.reset();
		int ch = 0;
		System.out.println("Enter students am: ");
		String am = sc.next();
		sc.nextLine();
		System.out.println("Enter Course Code: ");
		String course_code = sc.nextLine();
		
		if(!db.findCourse(course_code)) {
			System.out.println("Course not found");
			return;
		}
		
		if(!db.printStudentScores(db.findStudent(am), course_code)) {
			System.out.println("No Registry for Student with am: " + am + " found for Course: " + course_code);
		}
	}
	
	
	static void changeGrade() {
		sc.reset();
		int ch = 0;
		System.out.println("Enter students am: ");
		String am = sc.next();
		sc.nextLine();
		System.out.println("Enter Course Code: ");
		String course_code = sc.nextLine();
		System.out.println("Enter Course Serial Number: ");
		int serial = sc.nextInt();
		if(!db.findCourse(course_code, serial)) {
			System.out.println("Course not found");
			return;
		}
		System.out.println("Enter new Grade: ");
		int grade = sc.nextInt();
		
		if(!db.updateStudentScores(db.findStudent(am), course_code, serial, grade)) {
			System.out.println("No Registry for Student with am: " + am + " found for Course: " + course_code);
		}
				
	}
	
	
	private static void searchPerson() {
		sc.reset();
		int ch = 0;
		System.out.println("Enter Person's last name: ");
		String lname = sc.next();
		presentList(db.findPerson(lname));
	}
	
	
	private static void presentList(ArrayList<String[]> list) {
		if (list.isEmpty()) {
			System.out.println("0 Persons found.");
			return;
		}
		// less than 5 no need for pages
		else if(list.size()<=5) {
			for (int i = 0; i < list.size(); i++) {
				String[] person = list.get(i);
				System.out.println(String.format("%-1s. Surname: %-16s Name: %-16s Father Name: %-12s Amka: %-12s Email: %s",
		                (i + 1), person[3], person[1], person[2], person[0], person[4]));
			}
		}
		// inputs to pages
		else {
			sc.reset();
			int ch = 0;
			System.out.println(list.size() + " persons found. Enter the desired number of persons per page: ");
			int i = sc.nextInt();
			int numOfPages = list.size()/i;
			
			String input = "";
			int pagecounter = 0;
			// loop to present each page
			while(1!=0) {
				System.out.println(numOfPages + " pages. Enter number Of Page or 'n' for next page or 'x' to exit.");
				if (sc.hasNextInt()) {
					int x = sc.nextInt();
					if (x <= numOfPages) {
						pagecounter = x;
						presentPage(pagecounter, i, list);
					}
				}
				else if (sc.next().equals("x")) {
					break;
				}
				else if (sc.next().equals("n")) {
				    if(pagecounter < numOfPages) {
				    	pagecounter ++;
				    	presentPage(pagecounter, i, list);
				    }
				    else {
				    	System.out.println("Last page reached.");
				    	break;
				    }
				} else {
					System.out.println("No valid input..");
				}
			}
		}
	}



	private static void presentPage(int pagecounter, int i, ArrayList<String[]> list) {
		// calculate entries to be printed
		int fentry = i*(pagecounter);
		int lentry = i*(pagecounter + 1) - 1;
		for (int y = fentry; y < lentry; y++) {
			String[] person = list.get(y);
			System.out.println(String.format("%-1s. Surname: %-16s Name: %-16s Father Name: %-12s Amka: %-12s Email: %s",
	                (y + 1), person[3], person[1], person[2], person[0], person[4]));
		}	
	}
	
	
	private static void viewAnalGrades() {
		sc.reset();
		int ch = 0;
		System.out.println("Enter students am: ");
		String am = sc.next();
		presentGrades(db.getGrades(db.findStudent(am)));
		
	}
	
	
	private static void presentGrades(ArrayList<Object[]> list) {
		int size = list.size();
		int x = 0;
		int sem = 0;
		for(int i = 0; i < size; i++) {
			Object[] grades = list.get(i);
			
			if(x != (int) grades[4]) {
				sem++;
				x = (int) grades[4];
				System.out.println("-----------------------------------------------Semester " + sem + "------------------------------------------------");
			}
			System.out.println(String.format("%-1s. Course: %-16s Lab Grade: %-16s Exam Grade: %-12s Final_grade: %s",
	                (i + 1), grades[0], grades[3], grades[1], grades[2]));
			
		}
	}


}
