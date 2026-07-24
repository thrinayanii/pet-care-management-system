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
    $email    = trim($_POST['email']);
    $userPass = trim($_POST['password']);

    // Queries the database populated by your Java UserDao
    $stmt = $conn->prepare("SELECT id, first_name, role FROM users WHERE email = ? AND password_hash = ?");
    $stmt->bind_param("ss", $email, $userPass);
    $stmt->execute();
    $result = $stmt->get_result();

    if ($row = $result->fetch_assoc()) {
        $_SESSION['user_id']   = $row['id'];
        $_SESSION['user_role'] = strtolower($row['role']);
        $_SESSION['user_name'] = $row['first_name'];

        $stmt->close();
        $conn->close();

        // Direct paths to YOUR existing .html dashboard files shown in VS Code:
        switch ($_SESSION['user_role']) {
            case 'admin':
                header("Location: ../admin/admin_dashboard.html");
                break;
            case 'volunteer':
                header("Location: ../volunteer/volunteer_dashboard.html");
                break;
            default: // Pet Owner
                header("Location: ../pet_owner/petowner_dashboard.html");
                break;
        }
        exit();
    } else {
        $stmt->close();
        $conn->close();
        header("Location: login.html?error=invalid_credentials");
        exit();
    }
}
?>