<?php
session_start();
header('Content-Type: application/json');

if (!isset($_SESSION['user_id'])) {
    echo json_encode(['error' => 'Not authenticated']);
    exit();
}

$userId = $_SESSION['user_id'];

// Fetch profile data via Java DashboardDao / MainController
$cmd = "java -cp \"../bin;../lib/*\" com.pawcare.MainController getUserDashboard " . escapeshellarg($userId);
$output = shell_exec($cmd);

echo $output;
?>