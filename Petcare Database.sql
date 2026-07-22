CREATE DATABASE petcare_db;
USE petcare_db;

-- =====================================
-- USERS
-- =====================================

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    role ENUM('owner','admin','employee','volunteer','user') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================
-- EMPLOYEES
-- =====================================

CREATE TABLE employees (
    employee_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    employment_type ENUM('full_time','part_time') NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Employee can have multiple roles

CREATE TABLE employee_roles (
    role_id INT AUTO_INCREMENT PRIMARY KEY,
    employee_id INT NOT NULL,
    role ENUM(
        'groomer',
        'trainer',
        'walker',
        'care_taker',
        'vet'
    ) NOT NULL,
    FOREIGN KEY (employee_id) REFERENCES employees(employee_id)
);

-- =====================================
-- VOLUNTEERS
-- =====================================

CREATE TABLE volunteers (
    volunteer_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    preferred_task ENUM(
        'care_taking',
        'walking'
    ) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- =====================================
-- USER PETS
-- =====================================

CREATE TABLE user_pets (
    pet_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    species VARCHAR(50),
    breed VARCHAR(100),
    age INT,
    gender ENUM('male','female','unknown') DEFAULT 'unknown',
    notes TEXT,
    FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE
);

-- =====================================
-- RESCUE PETS
-- =====================================

CREATE TABLE rescue_pets (
    pet_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    species VARCHAR(50),
    breed VARCHAR(100),
    age INT,
    gender ENUM('male','female'),
    description TEXT,
    status ENUM(
        'available',
        'adopted',
        'under_care'
    ) DEFAULT 'available',
    intake_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================
-- OTHER SERVICES
-- =====================================

CREATE TABLE other_services (
    service_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    category ENUM(
        'grooming',
        'training',
        'boarding',
        'daycare',
        'walking',
        'other'
    ) NOT NULL,
    description TEXT,
    price DECIMAL(10,2),
    duration_minutes INT,
    available BOOLEAN DEFAULT TRUE
);

-- =====================================
-- VETERINARY SERVICES
-- =====================================

CREATE TABLE vet_services (
    service_id INT AUTO_INCREMENT PRIMARY KEY,
    service_type ENUM(
        'vaccination',
        'checkup',
        'emergency'
    ) NOT NULL,
    description TEXT,
    price DECIMAL(10,2),
    duration_minutes INT,
    available BOOLEAN DEFAULT TRUE
);

-- =====================================
-- OTHER SERVICE APPOINTMENTS
-- =====================================

CREATE TABLE service_appointments (
    appointment_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    pet_id INT,
    service_id INT NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    status ENUM(
        'pending',
        'confirmed',
        'completed',
        'cancelled'
    ) DEFAULT 'pending',
    notes TEXT,
    assigned_employee_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id)
        REFERENCES users(id),

    FOREIGN KEY (pet_id)
        REFERENCES user_pets(pet_id)
        ON DELETE SET NULL,

    FOREIGN KEY (service_id)
        REFERENCES other_services(service_id),

    FOREIGN KEY (assigned_employee_id)
        REFERENCES employees(employee_id)
        ON DELETE SET NULL
);

-- =====================================
-- VET APPOINTMENTS
-- =====================================

CREATE TABLE vet_appointments (
    appointment_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    pet_id INT,
    service_id INT NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    status ENUM(
        'pending',
        'confirmed',
        'completed',
        'cancelled'
    ) DEFAULT 'pending',
    notes TEXT,
    assigned_vet INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id)
        REFERENCES users(id),

    FOREIGN KEY (pet_id)
        REFERENCES user_pets(pet_id)
        ON DELETE SET NULL,

    FOREIGN KEY (service_id)
        REFERENCES vet_services(service_id),

    FOREIGN KEY (assigned_vet)
        REFERENCES employees(employee_id)
        ON DELETE SET NULL
);

-- =====================================
-- RECEIPTS
-- =====================================

CREATE TABLE receipts (
    receipt_id INT AUTO_INCREMENT PRIMARY KEY,
    appointment_type ENUM(
        'service',
        'vet'
    ) NOT NULL,

    service_appointment_id INT NULL,
    vet_appointment_id INT NULL,

    amount DECIMAL(10,2) NOT NULL,

    payment_method ENUM(
        'cash',
        'card',
        'bank_transfer'
    ) NOT NULL,

    payment_status ENUM(
        'pending',
        'paid',
        'refunded'
    ) DEFAULT 'pending',

    issued_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    transaction_reference VARCHAR(100),

    FOREIGN KEY (service_appointment_id)
        REFERENCES service_appointments(appointment_id),

    FOREIGN KEY (vet_appointment_id)
        REFERENCES vet_appointments(appointment_id)
);

-- =====================================
-- ADOPTION APPLICATIONS
-- =====================================

CREATE TABLE adoption_applications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    rescue_pet_id INT NOT NULL,
    message TEXT,
    status ENUM(
        'pending',
        'approved',
        'rejected'
    ) DEFAULT 'pending',
    applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id)
        REFERENCES users(id),

    FOREIGN KEY (rescue_pet_id)
        REFERENCES rescue_pets(pet_id)
);

-- =====================================
-- VOLUNTEER SHIFTS
-- =====================================

CREATE TABLE volunteer_shifts (
    shift_id INT AUTO_INCREMENT PRIMARY KEY,
    volunteer_id INT NOT NULL,
    task_type ENUM(
        'boarding',
        'daycare',
        'dog_walking'
    ) NOT NULL,
    shift_date DATE NOT NULL,
    shift_time TIME NOT NULL,
    notes TEXT,
    status ENUM(
        'scheduled',
        'completed',
        'cancelled'
    ) DEFAULT 'scheduled',

    FOREIGN KEY (volunteer_id)
        REFERENCES volunteers(volunteer_id)
);

-- =====================================
-- EVENTS
-- =====================================

CREATE TABLE events (
    event_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    type ENUM(
        'adoption_day',
        'community',
        'rescue',
        'other'
    ) NOT NULL,
    description TEXT,
    location VARCHAR(200),
    event_date DATE NOT NULL,
    event_time TIME,
    max_capacity INT,
    created_by INT,

    FOREIGN KEY (created_by)
        REFERENCES users(id)
        ON DELETE SET NULL
);

-- =====================================
-- EVENT REGISTRATIONS
-- =====================================

CREATE TABLE event_registrations (
    registration_id INT AUTO_INCREMENT PRIMARY KEY,
    event_id INT NOT NULL,
    user_id INT NOT NULL,
    registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (event_id)
        REFERENCES events(event_id),

    FOREIGN KEY (user_id)
        REFERENCES users(id)
);

-- =====================================
-- LOST & FOUND REPORTS
-- =====================================

CREATE TABLE lost_found_reports (
    report_id INT AUTO_INCREMENT PRIMARY KEY,
    reporter_name VARCHAR(150),
    reporter_phone VARCHAR(20),
    reporter_email VARCHAR(150),

    report_type ENUM(
        'lost',
        'found'
    ) NOT NULL,

    pet_name VARCHAR(100),
    species VARCHAR(50),
    breed VARCHAR(100),
    description TEXT,

    last_seen_location VARCHAR(200),

    report_date DATE DEFAULT (CURRENT_DATE),

    status ENUM(
        'open',
        'resolved'
    ) DEFAULT 'open'
);

-- =====================================
-- SURRENDER REQUESTS
-- =====================================

CREATE TABLE surrender_inquiries (
    inquiry_id INT AUTO_INCREMENT PRIMARY KEY,

    submitter_name VARCHAR(150) NOT NULL,
    submitter_phone VARCHAR(20),
    submitter_email VARCHAR(150),

    pet_name VARCHAR(100),
    species VARCHAR(50),
    breed VARCHAR(100),
    age INT,

    reason TEXT,

    status ENUM(
        'pending',
        'accepted',
        'declined'
    ) DEFAULT 'pending',

    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================
-- CENTER CAPACITY
-- =====================================

CREATE TABLE center_capacity (
    id INT AUTO_INCREMENT PRIMARY KEY,

    category ENUM(
        'boarding',
        'daycare',
        'rescue_housing'
    ) UNIQUE NOT NULL,

    total_slots INT NOT NULL,

    used_slots INT DEFAULT 0
);
