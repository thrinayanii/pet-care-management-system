package com.pawcare;

import com.pawcare.dao.AdminDao;
import com.pawcare.dao.AdoptionDao;
import com.pawcare.dao.AppointmentDao;
import com.pawcare.dao.DashboardDao;
import com.pawcare.dao.PetDao;
import com.pawcare.dao.RescuePetDao;
import com.pawcare.dao.UserDao;
import com.pawcare.dao.VolunteerDao;
import java.util.List;

public class MainController {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("{\"error\":\"No action specified\"}");
            return;
        }

        String action = args[0];

        switch (action) {
            case "addPet": {
                if (args.length >= 8) {
                    int userId = Integer.parseInt(args[1]);
                    String name = args[2];
                    String species = args[3];
                    String breed = args[4];
                    String gender = args[5];
                    String age = args[6];
                    String notes = args[7];

                    PetDao petDao = new PetDao();
                    boolean success = petDao.addPet(userId, name, species, breed, gender, age, notes);
                    System.out.println("{\"success\":" + success + "}");
                } else {
                    System.out.println("{\"success\":false,\"error\":\"Insufficient arguments\"}");
                }
                break;
            }

            case "getUserPets": {
                int userId = Integer.parseInt(args[1]);
                PetDao petDao = new PetDao();
                List<String> pets = petDao.getPetsByUserId(userId);
                System.out.println("{\"pets\":[" + String.join(",", pets) + "]}");
                break;
            }

            case "getUserDashboard": {
                int userId = Integer.parseInt(args[1]);
                DashboardDao dashDao = new DashboardDao();
                String dashboardJson = dashDao.getDashboardData(userId);
                System.out.println(dashboardJson);
                break;
            }

            case "bookAppointment": {
                int userId = Integer.parseInt(args[1]);
                int petId = Integer.parseInt(args[2]);
                String serviceType = args[3];
                String date = args[4];
                String time = args[5];
                String notes = args.length > 6 ? args[6] : "";

                AppointmentDao apptDao = new AppointmentDao();
                boolean success = apptDao.createAppointment(userId, petId, serviceType, date, time, notes);
                System.out.println("{\"success\":" + success + "}");
                break;
            }

            case "submitAdoption": {
                int userId = Integer.parseInt(args[1]);
                int petId = Integer.parseInt(args[2]);
                String housingType = args[3];
                String reason = args[4];

                AdoptionDao adoptionDao = new AdoptionDao();
                boolean success = adoptionDao.submitApplication(userId, petId, housingType, reason);
                System.out.println("{\"success\":" + success + "}");
                break;
            }

            case "getRescuePets": {
                RescuePetDao rescueDao = new RescuePetDao();
                List<String> pets = rescueDao.getAvailableRescuePets();
                System.out.println("{\"pets\":[" + String.join(",", pets) + "]}");
                break;
            }

            case "getUserAppointments": {
                int userId = Integer.parseInt(args[1]);
                AppointmentDao apptDao = new AppointmentDao();
                List<String> appts = apptDao.getAppointmentsByUserId(userId);
                System.out.println("{\"appointments\":[" + String.join(",", appts) + "]}");
                break;
            }

            case "updateProfile": {
                if (args.length >= 2) {
                    int userId = Integer.parseInt(args[1]);
                    String firstName = args.length > 2 ? args[2] : "";
                    String lastName  = args.length > 3 ? args[3] : "";
                    String email     = args.length > 4 ? args[4] : "";
                    String phone     = args.length > 5 ? args[5] : "";

                    UserDao userDao = new UserDao();
                    boolean success = userDao.updateUserProfile(userId, firstName, lastName, email, phone);
                    System.out.println("{\"success\":" + success + "}");
                } else {
                    System.out.println("{\"success\":false,\"error\":\"Missing user ID\"}");
                }
                break;
            }

            case "getVolunteerTasks": {
                VolunteerDao volDao = new VolunteerDao();
                List<String> tasks = volDao.getAvailableTasks();
                System.out.println("{\"tasks\":[" + String.join(",", tasks) + "]}");
                break;
            }

            case "applyVolunteerTask": {
                if (args.length >= 3) {
                    int userId = Integer.parseInt(args[1]);
                    int taskId = Integer.parseInt(args[2]);

                    VolunteerDao volDao = new VolunteerDao();
                    boolean success = volDao.applyForTask(userId, taskId);
                    System.out.println("{\"success\":" + success + "}");
                } else {
                    System.out.println("{\"success\":false,\"error\":\"Missing arguments\"}");
                }
                break;
            }

            case "cancelVolunteerTask": {
                if (args.length >= 3) {
                    int userId = Integer.parseInt(args[1]);
                    int requestId = Integer.parseInt(args[2]);

                    VolunteerDao volDao = new VolunteerDao();
                    boolean success = volDao.cancelShiftRequest(userId, requestId);
                    System.out.println("{\"success\":" + success + "}");
                } else {
                    System.out.println("{\"success\":false,\"error\":\"Missing arguments\"}");
                }
                break;
            }

            case "submitInquiry": {
                if (args.length >= 5) {
                    int userId = Integer.parseInt(args[1]);
                    String subject = args[2];
                    String category = args[3];
                    String message = args[4];

                    VolunteerDao volDao = new VolunteerDao();
                    boolean success = volDao.submitInquiry(userId, subject, category, message);
                    System.out.println("{\"success\":" + success + "}");
                } else {
                    System.out.println("{\"success\":false,\"error\":\"Missing arguments\"}");
                }
                break;
            }

            case "getVolunteerDashboard": {
                if (args.length >= 2) {
                    int userId = Integer.parseInt(args[1]);
                    VolunteerDao volDao = new VolunteerDao();
                    String dashboardJson = volDao.getVolunteerDashboardData(userId);
                    System.out.println(dashboardJson);
                } else {
                    System.out.println("{\"error\":\"Missing user ID\"}");
                }
                break;
            }

            case "getAdminDashboard": {
                AdminDao adminDao = new AdminDao();
                String adminJson = adminDao.getAdminDashboardData();
                System.out.println(adminJson);
                break;
            }

            case "updateShiftStatus": {
                if (args.length >= 3) {
                    int requestId = Integer.parseInt(args[1]);
                    String newStatus = args[2];

                    AdminDao adminDao = new AdminDao();
                    boolean success = adminDao.updateShiftStatus(requestId, newStatus);
                    System.out.println("{\"success\":" + success + "}");
                } else {
                    System.out.println("{\"success\":false,\"error\":\"Missing arguments\"}");
                }
                break;
            }
            
            default:
                System.out.println("{\"error\":\"Unknown action\"}");
                break;
        }
    }
}