<?php
session_start();

if (!isset($_SESSION['user_id'])) {
    header("Location: ../auth/login.html");
    exit();
}

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    $userId  = $_SESSION['user_id'];
    $name    = trim($_POST['name']);
    $species = trim($_POST['species']);
    $breed   = trim($_POST['breed']);
    $gender  = trim($_POST['gender']);
    $age     = trim($_POST['age']);
    $notes   = isset($_POST['notes']) ? trim($_POST['notes']) : '';

    $cmd = "java -cp \"../bin;../lib/*\" com.pawcare.MainController addPet " . 
            escapeshellarg($userId) . " " . 
            escapeshellarg($name) . " " . 
            escapeshellarg($species) . " " . 
            escapeshellarg($breed) . " " . 
            escapeshellarg($gender) . " " . 
            escapeshellarg($age) . " " . 
            escapeshellarg($notes);

    $output = shell_exec($cmd);
    $res = json_decode($output, true);

    if (isset($res['success']) && $res['success'] === true) {
        // Redirect directly to My Pets tab on success
        header("Location: mypets.html?added=success");
    } else {
        header("Location: add_pet.html?error=failed");
    }
    exit();
}
?>