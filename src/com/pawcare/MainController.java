package com.pawcare;

import com.pawcare.dao.AdoptionDao;
import com.pawcare.dao.AppointmentDao;
import com.pawcare.dao.DashboardDao;
import com.pawcare.dao.PetDao;
import com.pawcare.dao.RescuePetDao;
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

            default:
                System.out.println("{\"error\":\"Unknown action\"}");
                break;
        }
    }
}