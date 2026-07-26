<?php
session_start();
header('Content-Type: application/json');

if (!isset($_SESSION['user_id'])) {
    echo json_encode(['success' => false, 'error' => 'Not authenticated']);
    exit();
}

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    $userId   = $_SESSION['user_id'];
    $subject  = trim($_POST['subject'] ?? '');
    $category = trim($_POST['category'] ?? '');
    $message  = trim($_POST['message'] ?? '');

    $cmd = "java -cp \"../bin;../lib/*\" com.pawcare.MainController submitInquiry " . 
            escapeshellarg($userId) . " " . 
            escapeshellarg($subject) . " " . 
            escapeshellarg($category) . " " . 
            escapeshellarg($message);

    $output = shell_exec($cmd);
    echo $output;
    exit();
}
?>