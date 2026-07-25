<?php
session_start();

if (!isset($_SESSION['user_id'])) {
    header("Location: ../auth/login.html");
    exit();
}

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    $userId      = $_SESSION['user_id'];
    $petId       = $_POST['pet_id'];
    $housingType = trim($_POST['housing_type']);
    $reason      = trim($_POST['reason']);

    // Pass application details to Java AdoptionDao
    $cmd = "java -cp \"../bin;../lib/*\" com.pawcare.MainController submitAdoption " .
            escapeshellarg($userId) . " " . 
            escapeshellarg($petId) . " " . 
            escapeshellarg($housingType) . " " . 
            escapeshellarg($reason);

    $output = shell_exec($cmd);
    $result = json_decode($output, true);

    if (isset($result['success']) && $result['success'] === true) {
        header("Location: petowner_dashboard.html?adoption=submitted");
    } else {
        header("Location: pet_adoption.html?error=failed");
    }
    exit();
}
?>