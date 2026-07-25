<?php
session_start();
header('Content-Type: application/json');

if (!isset($_SESSION['user_id'])) {
    echo json_encode(['error' => 'Not authenticated', 'appointments' => []]);
    exit();
}

$userId = $_SESSION['user_id'];

// Path relative to pet_owner/ -> bin and lib are in project root (../)
$cmd = "java -cp \"../bin;../lib/*\" com.pawcare.MainController getUserAppointments " . escapeshellarg($userId);
$output = shell_exec($cmd);

echo $output;
?>