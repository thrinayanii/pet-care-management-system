<?php
session_start();

$servername = "localhost";
$username   = "root";
$password   = "";
$dbname     = "petcare_db";

$conn = new mysqli($servername, $username, $password, $dbname);

if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    $firstName = trim($_POST['first_name']);
    $lastName  = trim($_POST['last_name']);
    $email     = trim($_POST['email']);
    $userPass  = trim($_POST['password']);
    $phone     = trim($_POST['phone']);
    $role      = isset($_POST['role']) ? strtolower(trim($_POST['role'])) : 'user';

    // 1. Insert into users table
    $stmt = $conn->prepare("INSERT INTO users (first_name, last_name, email, password_hash, phone, role) VALUES (?, ?, ?, ?, ?, ?)");
    $stmt->bind_param("ssssss", $firstName, $lastName, $email, $userPass, $phone, $role);

    if ($stmt->execute()) {
        $userId = $stmt->insert_id;

        // 2. Insert volunteer or pet details
        if ($role === 'volunteer') {
            $preferredTask = !empty($_POST['preferred_task']) ? $_POST['preferred_task'] : 'daycare';
            $vStmt = $conn->prepare("INSERT INTO volunteers (user_id, preferred_task) VALUES (?, ?)");
            $vStmt->bind_param("is", $userId, $preferredTask);
            $vStmt->execute();
            $vStmt->close();
        } else if ($role === 'user' && !empty($_POST['pet_name'])) {
            $petName = trim($_POST['pet_name']);
            $pStmt = $conn->prepare("INSERT INTO user_pets (user_id, name) VALUES (?, ?)");
            $pStmt->bind_param("is", $userId, $petName);
            $pStmt->execute();
            $pStmt->close();
        }

        $stmt->close();
        $conn->close();

        // 3. Set Session & Redirect directly to your HTML dashboards
        $_SESSION['user_id']   = $userId;
        $_SESSION['user_role'] = $role;
        $_SESSION['user_name'] = $firstName;

        switch ($role) {
            case 'admin':
                header("Location: ../admin/admin_dashboard.html");
                break;
            case 'volunteer':
                header("Location: ../volunteer/volunteer_dashboard.html");
                break;
            default:
                header("Location: ../pet_owner/petowner_dashboard.html");
                break;
        }
        exit();
    } else {
        $stmt->close();
        $conn->close();
        header("Location: register.html?error=failed");
        exit();
    }
}
?>