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
    $firstName = trim($_POST['first_name'] ?? '');
    $lastName  = trim($_POST['last_name'] ?? '');
    $email     = trim($_POST['email'] ?? '');
    $userPass  = trim($_POST['password'] ?? '');
    $phone     = trim($_POST['phone'] ?? '');
    $role      = isset($_POST['role']) ? strtolower(trim($_POST['role'])) : 'user';

    // 1. Check if email already exists
    $checkStmt = $conn->prepare("SELECT id FROM users WHERE email = ?");
    $checkStmt->bind_param("s", $email);
    $checkStmt->execute();
    $checkStmt->store_result();
    if ($checkStmt->num_rows > 0) {
        die("<h3>Registration Failed!</h3><p>An account with the email <strong>" . htmlspecialchars($email) . "</strong> already exists. Please use a different email or log in.</p>");
    }
    $checkStmt->close();

    // 2. Insert into users table
    $stmt = $conn->prepare("INSERT INTO users (first_name, last_name, email, password_hash, phone, role) VALUES (?, ?, ?, ?, ?, ?)");
    if (!$stmt) {
        die("<h3>Prepare Failed!</h3><p>" . $conn->error . "</p>");
    }
    
    $stmt->bind_param("ssssss", $firstName, $lastName, $email, $userPass, $phone, $role);

    if ($stmt->execute()) {
        $userId = $stmt->insert_id;

        // 3. Insert volunteer details
        if ($role === 'volunteer') {
            // Read preferred_service_id or preferred_task from form submit
            $rawPref = $_POST['preferred_service_id'] ?? $_POST['preferred_task'] ?? 4;
            $prefServiceId = (int) $rawPref;
            if ($prefServiceId === 0) $prefServiceId = 4; // Fallback to service_id 4

            $vStmt = $conn->prepare("INSERT INTO volunteers (user_id, preferred_service_id) VALUES (?, ?)");
            if (!$vStmt) {
                die("<h3>Volunteer Prepare Failed!</h3><p>" . $conn->error . "</p>");
            }
            $vStmt->bind_param("ii", $userId, $prefServiceId);
            
            if (!$vStmt->execute()) {
                die("<h3>Volunteer Insert Failed!</h3><p>" . $vStmt->error . "</p>");
            }
            $vStmt->close();
        } else if ($role === 'user' && !empty($_POST['pet_name'])) {
            $petName = trim($_POST['pet_name']);
            $pStmt = $conn->prepare("INSERT INTO user_pets (user_id, name) VALUES (?, ?)");
            if ($pStmt) {
                $pStmt->bind_param("is", $userId, $petName);
                $pStmt->execute();
                $pStmt->close();
            }
        }

        $stmt->close();
        $conn->close();

        // 4. Set Session & Redirect to Volunteer Dashboard
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
        $errorMsg = $stmt->error;
        $stmt->close();
        $conn->close();
        die("<h3>User Registration Failed!</h3><p>MySQL Error: " . $errorMsg . "</p>");
    }
}
?>