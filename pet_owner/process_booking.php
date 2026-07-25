<?php
session_start();

if (!isset($_SESSION['user_id'])) {
    header("Location: ../auth/login.html");
    exit();
}

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    $userId   = $_SESSION['user_id'];
    $petId    = $_POST['pet_id'];
    $service  = $_POST['service_type'];
    $apptDate = $_POST['appointment_date'];
    $apptTime = $_POST['appointment_time'];
    $notes    = isset($_POST['notes']) ? trim($_POST['notes']) : '';

    // Pass booking details to Java AppointmentDao
    $cmd = "java -cp \"../bin;../lib/*\" com.pawcare.MainController bookAppointment " . 
            escapeshellarg($userId) . " " . 
            escapeshellarg($petId) . " " . 
            escapeshellarg($service) . " " . 
            escapeshellarg($apptDate) . " " . 
            escapeshellarg($apptTime) . " " . 
            escapeshellarg($notes);

    $output = shell_exec($cmd);
    $result = json_decode($output, true);

    if (isset($result['success']) && $result['success'] === true) {
        header("Location: my_appointments.html?booking=success");
    } else {
        header("Location: book_service.html?error=failed");
    }
    exit();
}
?>