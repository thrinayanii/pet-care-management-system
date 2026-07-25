<?php
session_start();
header('Content-Type: application/json');

$conn = new mysqli("localhost", "root", "", "petcare_db");

if ($conn->connect_error) {
    echo json_encode(['error' => 'Database connection failed']);
    exit();
}

// 1. Get Vet Services
$vetRes = $conn->query("SELECT service_id, service_type AS name, 'vet' AS source, price FROM vet_services WHERE available = 1");
$vetServices = [];
while ($row = $vetRes->fetch_assoc()) {
    $vetServices[] = $row;
}

// 2. Get Other Services
$otherRes = $conn->query("SELECT service_id, name, category AS source, price FROM other_services WHERE available = 1");
$otherServices = [];
while ($row = $otherRes->fetch_assoc()) {
    $otherServices[] = $row;
}

echo json_encode([
    'vet_services'   => $vetServices,
    'other_services' => $otherServices
]);

$conn->close();
?>