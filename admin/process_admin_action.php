<?php
session_start();
header('Content-Type: application/json');

if (!isset($_SESSION['user_id'])) {
    echo json_encode(['success' => false, 'error' => 'Not authenticated']);
    exit();
}

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    $requestId = (int) ($_POST['request_id'] ?? 0);
    $status    = trim($_POST['status'] ?? '');

    $cmd = "java -cp \"../bin;../lib/*\" com.pawcare.MainController updateShiftStatus " . 
            escapeshellarg($requestId) . " " . 
            escapeshellarg($status);

    $output = shell_exec($cmd);
    echo trim($output);
    exit();
}
?>