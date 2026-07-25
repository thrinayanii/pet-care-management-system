<?php
session_start();
header('Content-Type: application/json');

if (!isset($_SESSION['user_id'])) {
    echo json_encode(['error' => 'Not authenticated', 'pets' => []]);
    exit();
}

$userId = $_SESSION['user_id'];

// Path to compiled Java classes (.class files inside bin or build folder)
$cmd = "java -cp \"../bin;../lib/*\" com.pawcare.MainController getUserPets " . escapeshellarg($userId);
$output = shell_exec($cmd);

echo $output;
?>