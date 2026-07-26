<?php
session_start();
header('Content-Type: application/json');

if (!isset($_SESSION['user_id'])) {
    echo json_encode(['success' => false, 'error' => 'Not authenticated']);
    exit();
}

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    $userId = (int) $_SESSION['user_id'];
    $action = $_POST['action'] ?? 'apply';
    $id     = (int) ($_POST['id'] ?? 0);

    if ($action === 'cancel') {
        $cmd = "java -cp \"../bin;../lib/*\" com.pawcare.MainController cancelVolunteerTask " . 
                escapeshellarg($userId) . " " . 
                escapeshellarg($id);
    } else {
        $cmd = "java -cp \"../bin;../lib/*\" com.pawcare.MainController applyVolunteerTask " . 
                escapeshellarg($userId) . " " . 
                escapeshellarg($id);
    }

    $output = shell_exec($cmd);
    echo trim($output);
    exit();
}
?>