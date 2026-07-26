<?php
session_start();
header('Content-Type: application/json');

if (!isset($_SESSION['user_id'])) {
    echo json_encode(['error' => 'Not authenticated']);
    exit();
}

$cmd = "java -cp \"../bin;../lib/*\" com.pawcare.MainController getAdminDashboard";
$output = shell_exec($cmd);

echo trim($output);
?>