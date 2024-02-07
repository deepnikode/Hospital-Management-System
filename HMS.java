import java.sql.*;
import java.util.Scanner;

public class HMS
    {

        private static final String url="jdbc:mysql://localhost:3306/hospital";
        private static final String username="root";
        private static final String password="password";

        public static void main(String[] args) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            }
            catch (ClassNotFoundException e)
            {
                e.printStackTrace();
            }
            Scanner scanner=new Scanner(System.in);
            try {
                Connection connection= DriverManager.getConnection(url,username,password);
                Patient patient=new Patient(connection,scanner);
                Doctor doctor=new Doctor(connection);

                while(true)
                {
                    System.out.println("HOSPITAL MANAGEMENT SYSTEM");
                    System.out.println("1. Add Patient");
                    System.out.println("2. View Patient");
                    System.out.println("3. View Doctor");
                    System.out.println("4. Book Appointment");
                    System.out.println("5. Exit");

                    System.out.println("Enter your Choice");
                    int choice=scanner.nextInt();

                    switch (choice)
                    {
                        case 1:
                            //Add Patient
                            patient.addPatient();
                            System.out.println();
                        case 2:
                            //view Patient
                            patient.viewPatients();
                            System.out.println();
                        case 3:
                            //view Doctor
                            doctor.viewDoctor();
                            System.out.println();
                        case 4:
                            //Book Appointment
                            bookAppointment(patient,doctor,connection,scanner);
                            System.out.println();
                        case 5:
                            return;
                        default:
                            System.out.println("Enter valid Choice");
                    }

                }

            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }

        public static void bookAppointment(Patient patient, Doctor doctor, Connection connection, Scanner scanner)
        {
            System.out.println("Enter Patient's Id: ");
            int patientId=scanner.nextInt();
            System.out.println("Enter Doctor's Id: ");
            int doctorId=scanner.nextInt();
            System.out.println("Enter Appointment Date (YYYY-MM-DD): ");
            String appointmentDate=scanner.next();

            if (patient.getPatientById(patientId)&& doctor.getDoctorById(doctorId))
            {
               if(checkDoctorAvailiblity(doctorId,appointmentDate,connection))
               {
                  String appointmentQuery="insert into appointments(patient_id,doctor_id,appointment_date) values(?,?,?)";
                  try
                  {
                      PreparedStatement preparedStatement=connection.prepareStatement(appointmentQuery);
                      preparedStatement.setInt(1,patientId);
                      preparedStatement.setInt(2,doctorId);
                      preparedStatement.setString(3,appointmentDate);
                      int rowsAffected=preparedStatement.executeUpdate();
                      if(rowsAffected>0)
                      {
                          System.out.println("Appointment Booked");
                      }
                      else
                      {
                          System.out.println("Failed to book appointment");
                      }

                  }
                  catch (SQLException e)
                  {
                      e.printStackTrace();
                  }
               }
               else
               {
                   System.out.println("Doctor's not available on this date");
               }
            }
            else
            {
                System.out.println("Either Doctor or Patient Doesn't exist!!!");
            }

        }

        public static boolean checkDoctorAvailiblity(int doctorId, String appointmentDate, Connection connection)
        {
            String query="select count(*) from appointments where doctor_id=? and appointment_date=?";
            try {
                PreparedStatement preparedStatement= connection.prepareStatement(query);
                preparedStatement.setInt(1,doctorId);
                preparedStatement.setString(2,appointmentDate);
                ResultSet resultSet=preparedStatement.executeQuery();
                if (resultSet.next())
                {
                    int count=resultSet.getInt(1);
                    if (count==0)
                    {
                        return true;
                    }
                    else
                    {
                        return false;
                    }
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
            return false;
        }
    }

