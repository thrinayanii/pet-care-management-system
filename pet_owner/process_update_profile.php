<?php
session_start();

if (!isset($_SESSION['user_id'])) {
    header("Location: ../auth/login.html");
    exit();
}

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    $userId    = $_SESSION['user_id'];
    $firstName = trim($_POST['first_name']);
    $lastName  = trim($_POST['last_name']);
    $email     = trim($_POST['email']);
    $phone     = trim($_POST['phone']);

    $cmd = "java -cp \"../bin;../lib/*\" com.pawcare.MainController updateProfile " . 
            escapeshellarg($userId) . " " . 
            escapeshellarg($firstName) . " " . 
            escapeshellarg($lastName) . " " . 
            escapeshellarg($email) . " " . 
            escapeshellarg($phone);

    $output = shell_exec($cmd);
    $res = json_decode($output, true);

    if (isset($res['success']) && $res['success'] === true) {
        $_SESSION['user_name'] = $firstName;
        header("Location: profile.html?updated=success");
    } else {
        header("Location: profile.html?error=failed");
    }
    exit();
}
?>