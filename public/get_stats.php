<?php
header('Content-Type: application/json');
$conn = new mysqli("localhost", "root", "", "petcare_db");

if ($conn->connect_error) {
    echo json_encode(['registered_pets' => 0, 'rescue_pets' => 0]);
    exit();
}

// 1. Count Total Pets Registered by Pet Owners
$res1 = $conn->query("SELECT COUNT(*) AS total FROM user_pets");
$userPetsCount = $res1 ? $res1->fetch_assoc()['total'] : 0;

// 2. Count Total Rescue Pets
$res2 = $conn->query("SELECT COUNT(*) AS total FROM rescue_pets");
$rescuePetsCount = $res2 ? $res2->fetch_assoc()['total'] : 0;

echo json_encode([
    'registered_pets' => (int)$userPetsCount,
    'rescue_pets'     => (int)$rescuePetsCount,
    'total_pets'      => (int)($userPetsCount + $rescuePetsCount)
]);
$conn->close();
?>