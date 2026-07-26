-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jul 26, 2026 at 05:50 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `petcare_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `adoption_applications`
--

CREATE TABLE `adoption_applications` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `pet_id` int(11) NOT NULL,
  `housing_type` varchar(100) DEFAULT NULL,
  `reason` text DEFAULT NULL,
  `status` enum('pending','approved','rejected') DEFAULT 'pending',
  `applied_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `adoption_applications`
--

INSERT INTO `adoption_applications` (`id`, `user_id`, `pet_id`, `housing_type`, `reason`, `status`, `applied_at`) VALUES
(1, 5, 15, 'House', 'I want to adopt an athletic boxer breed ', 'pending', '2026-07-25 17:49:00');

-- --------------------------------------------------------

--
-- Table structure for table `center_capacity`
--

CREATE TABLE `center_capacity` (
  `id` int(11) NOT NULL,
  `category` enum('boarding','daycare','rescue_housing') NOT NULL,
  `total_slots` int(11) NOT NULL,
  `used_slots` int(11) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `center_capacity`
--

INSERT INTO `center_capacity` (`id`, `category`, `total_slots`, `used_slots`) VALUES
(1, 'boarding', 20, 0),
(2, 'daycare', 15, 0),
(3, 'rescue_housing', 30, 0);

-- --------------------------------------------------------

--
-- Table structure for table `employees`
--

CREATE TABLE `employees` (
  `employee_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `employment_type` enum('full_time','part_time') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `employee_roles`
--

CREATE TABLE `employee_roles` (
  `role_id` int(11) NOT NULL,
  `employee_id` int(11) NOT NULL,
  `role` enum('groomer','trainer','walker','care_taker','vet') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `other_services`
--

CREATE TABLE `other_services` (
  `service_id` int(11) NOT NULL,
  `name` varchar(150) NOT NULL,
  `category` enum('grooming','training','boarding','daycare','walking','other') NOT NULL,
  `description` text DEFAULT NULL,
  `price` decimal(10,2) DEFAULT NULL,
  `duration_minutes` int(11) DEFAULT NULL,
  `available` tinyint(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `other_services`
--

INSERT INTO `other_services` (`service_id`, `name`, `category`, `description`, `price`, `duration_minutes`, `available`) VALUES
(1, 'Grooming & Bathing', 'grooming', 'Full bath, hair trim, coat conditioning, and nail trimming.', 3200.00, 60, 1),
(2, 'Pet Daycare', 'daycare', 'Full day supervised care, outdoor play, and feeding.', 2500.00, 480, 1),
(3, 'Overnight Boarding', 'boarding', 'Safe overnight accommodation with dedicated staff care.', 4000.00, 1440, 1),
(4, 'Dog Walking Session', 'walking', '45-minute active outdoor neighborhood walking session.', 1800.00, 45, 1),
(5, 'Obedience Training', 'training', '1-on-1 basic command and behavior modification training.', 5000.00, 60, 1);

-- --------------------------------------------------------

--
-- Table structure for table `receipts`
--

CREATE TABLE `receipts` (
  `receipt_id` int(11) NOT NULL,
  `appointment_type` enum('service','vet') NOT NULL,
  `service_appointment_id` int(11) DEFAULT NULL,
  `vet_appointment_id` int(11) DEFAULT NULL,
  `amount` decimal(10,2) NOT NULL,
  `payment_method` enum('cash','card','bank_transfer') NOT NULL,
  `payment_status` enum('pending','paid','refunded') DEFAULT 'pending',
  `issued_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `transaction_reference` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `rescue_pets`
--

CREATE TABLE `rescue_pets` (
  `pet_id` int(11) NOT NULL,
  `kennel_no` varchar(20) DEFAULT NULL,
  `name` varchar(100) NOT NULL,
  `species` varchar(50) DEFAULT NULL,
  `breed` varchar(100) DEFAULT NULL,
  `age_display` varchar(50) DEFAULT NULL,
  `age` int(11) DEFAULT NULL,
  `gender` enum('male','female') DEFAULT NULL,
  `trait` varchar(100) DEFAULT NULL,
  `description` text DEFAULT NULL,
  `status` enum('available','adopted','under_care') DEFAULT 'available',
  `intake_date` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `rescue_pets`
--

INSERT INTO `rescue_pets` (`pet_id`, `kennel_no`, `name`, `species`, `breed`, `age_display`, `age`, `gender`, `trait`, `description`, `status`, `intake_date`) VALUES
(1, 'K-01', 'Bruno', 'Dog', 'Indie Cross', '1 Year 6 Months', 1, 'male', 'Friendly & Playful', 'Energetic pup who loves playing fetch and gets along well with other dogs.', 'available', '2026-01-15'),
(2, 'K-02', 'Luna', 'Cat', 'Domestic Short Hair', '10 Months', 0, 'female', 'Gentle & Cuddly', 'Affectionate indoor cat who loves warm sunny spots and lap cuddles.', 'available', '2026-02-01'),
(3, 'K-03', 'Rocky', 'Dog', 'Labrador Mix', '3 Years', 3, 'male', 'Calm & Family Friendly', 'Well-trained rescue dog looking for a loving home. Great with kids.', 'available', '2026-01-10'),
(4, 'K-04', 'Mimi', 'Cat', 'Calico', '6 Months', 0, 'female', 'Curious & Vocal', 'Playful kitten rescued from a park. Loves interactive feather toys.', 'available', '2026-03-05'),
(5, 'K-05', 'Cooper', 'Dog', 'Beagle Cross', '2 Years', 2, 'male', 'Silly & Active', 'Affectionate hound who loves outdoor walks, tracking scents, and treats.', 'available', '2026-02-14'),
(6, 'K-06', 'Cleo', 'Cat', 'Siamese Mix', '1 Year 2 Months', 1, 'female', 'Quiet & Sweet', 'Observant cat with blue eyes. Shy at first but very affectionate once comfortable.', 'available', '2026-03-12'),
(7, 'K-07', 'Max', 'Dog', 'German Shepherd Mix', '4 Years', 4, 'male', 'Loyal & Intelligent', 'Quick learner who responds wonderfully to basic obedience commands.', 'available', '2025-11-20'),
(8, 'K-08', 'Bella', 'Cat', 'Tabby', '2 Years', 2, 'female', 'Peaceful & Loving', 'Sweet tabby who enjoys quiet environments and gentle head scratches.', 'available', '2026-01-22'),
(9, 'K-09', 'Teddy', 'Dog', 'Poodle Mix', '8 Months', 0, 'male', 'Fluffy & Lively', 'Active and fluffy little pup looking for a loving, energetic household.', 'available', '2026-04-02'),
(10, 'K-10', 'Oliver', 'Cat', 'Ginger Domestic', '1 Year 8 Months', 1, 'male', 'Outgoing & Social', 'Friendly orange cat who loves greeting everyone and socializing.', 'available', '2026-02-28'),
(11, 'K-11', 'Daisy', 'Dog', 'Golden Retriever Mix', '2 Years 5 Months', 2, 'female', 'Gentle & Loving', 'Sweet rescue dog with a heart of gold. Loves water and belly rubs.', 'available', '2025-12-18'),
(12, 'K-12', 'Shadow', 'Cat', 'Bombay Mix', '3 Years', 3, 'male', 'Cozy & Playful', 'Sleek black cat who loves cozy blankets and playing with string toys.', 'available', '2026-03-15'),
(13, 'K-13', 'Charlie', 'Dog', 'Cocker Spaniel Mix', '1 Year', 1, 'male', 'Happy & Cheerful', 'Happy-go-lucky dog with floppy ears and an endlessly wagging tail.', 'available', '2026-04-10'),
(14, 'K-14', 'Hazel', 'Cat', 'Tortoiseshell', '9 Months', 0, 'female', 'Spirited & Energetic', 'Spirited kitten full of curiosity and fun antics.', 'available', '2026-03-25'),
(15, 'K-15', 'Duke', 'Dog', 'Boxer Mix', '3 Years 2 Months', 3, 'male', 'Athletic & Strong', 'High-energy dog who loves fetch games and long runs in open fields.', 'available', '2026-01-30'),
(16, 'K-16', 'Maya', 'Cat', 'Persian Mix', '2 Years', 2, 'female', 'Calm & Regal', 'Fluffy indoor cat who prefers a quiet home without loud noises.', 'available', '2026-02-18'),
(17, 'K-17', 'Milo', 'Dog', 'Jack Russell Mix', '1 Year 4 Months', 1, 'male', 'Smart & Curious', 'High-energy pup who loves learning new tricks and exploring outdoors.', 'available', '2026-04-01'),
(18, 'K-18', 'Nala', 'Cat', 'Domestic Long Hair', '1 Year 11 Months', 1, 'female', 'Gentle & Observant', 'Peaceful feline who loves lounging near sunny windows and birdwatching.', 'available', '2026-03-01');

-- --------------------------------------------------------

--
-- Table structure for table `service_appointments`
--

CREATE TABLE `service_appointments` (
  `appointment_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `pet_id` int(11) DEFAULT NULL,
  `service_id` int(11) NOT NULL,
  `appointment_date` date NOT NULL,
  `appointment_time` time NOT NULL,
  `status` enum('pending','confirmed','completed','cancelled') DEFAULT 'pending',
  `notes` text DEFAULT NULL,
  `assigned_employee_id` int(11) DEFAULT NULL,
  `assigned_volunteer_id` int(11) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `service_appointments`
--

INSERT INTO `service_appointments` (`appointment_id`, `user_id`, `pet_id`, `service_id`, `appointment_date`, `appointment_time`, `status`, `notes`, `assigned_employee_id`, `assigned_volunteer_id`, `created_at`) VALUES
(1, 5, 2, 1, '2026-07-27', '17:00:00', 'confirmed', 'none', NULL, NULL, '2026-07-25 08:42:45');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `first_name` varchar(100) NOT NULL,
  `last_name` varchar(100) NOT NULL,
  `email` varchar(150) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `role` enum('user','admin','employee','volunteer') NOT NULL DEFAULT 'user',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `first_name`, `last_name`, `email`, `password_hash`, `phone`, `role`, `created_at`) VALUES
(1, 'Thrinayani', 'Selvanathan', 'thrina@example.com', 'pass123', '0771234567', 'user', '2026-07-24 04:37:36'),
(4, 'John', 'Silva', 'js123@gmail.com', 'P@ssw0rd2026!', '07797979797', 'user', '2026-07-24 05:52:15'),
(5, 'Janet', 'Ruth', 'jr123@yahoo.com', 'zeroCola0_1', '07797979787', 'user', '2026-07-25 07:05:48'),
(6, 'Mary', 'Jane', 'mary2@gmail.com', 'zeroCola0_1', '07769696969', 'volunteer', '2026-07-25 19:40:29');

-- --------------------------------------------------------

--
-- Table structure for table `user_pets`
--

CREATE TABLE `user_pets` (
  `pet_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `species` varchar(50) DEFAULT NULL,
  `breed` varchar(100) DEFAULT NULL,
  `age` varchar(50) DEFAULT NULL,
  `gender` varchar(20) DEFAULT 'Unknown',
  `notes` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `user_pets`
--

INSERT INTO `user_pets` (`pet_id`, `user_id`, `name`, `species`, `breed`, `age`, `gender`, `notes`) VALUES
(1, 4, 'Dally', NULL, NULL, NULL, 'unknown', NULL),
(2, 5, 'Rallie', 'Cat', 'Bengal', '4 Months', 'Female', 'none');

-- --------------------------------------------------------

--
-- Table structure for table `vet_appointments`
--

CREATE TABLE `vet_appointments` (
  `appointment_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `pet_id` int(11) DEFAULT NULL,
  `service_id` int(11) NOT NULL,
  `appointment_date` date NOT NULL,
  `appointment_time` time NOT NULL,
  `status` enum('pending','confirmed','completed','cancelled') DEFAULT 'pending',
  `notes` text DEFAULT NULL,
  `assigned_vet` int(11) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `vet_services`
--

CREATE TABLE `vet_services` (
  `service_id` int(11) NOT NULL,
  `service_type` enum('vaccination','checkup','emergency') NOT NULL,
  `description` text DEFAULT NULL,
  `price` decimal(10,2) DEFAULT NULL,
  `duration_minutes` int(11) DEFAULT NULL,
  `available` tinyint(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `vet_services`
--

INSERT INTO `vet_services` (`service_id`, `service_type`, `description`, `price`, `duration_minutes`, `available`) VALUES
(1, '', 'General physical exam, health assessment, and medical advice.', 2000.00, 30, 1),
(2, '', 'Core rabies and DHPP/FVRCP vaccination shots.', 2500.00, 20, 1),
(3, '', 'Standard ISO microchip insertion and registration.', 1800.00, 15, 1),
(4, '', 'Ultrasonic teeth cleaning and oral health checkup.', 4500.00, 60, 1);

-- --------------------------------------------------------

--
-- Table structure for table `volunteers`
--

CREATE TABLE `volunteers` (
  `volunteer_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `preferred_task` enum('daycare','boarding','dog_walking') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `volunteers`
--

INSERT INTO `volunteers` (`volunteer_id`, `user_id`, `preferred_task`) VALUES
(1, 6, 'dog_walking');

-- --------------------------------------------------------

--
-- Table structure for table `volunteer_shifts`
--

CREATE TABLE `volunteer_shifts` (
  `shift_id` int(11) NOT NULL,
  `volunteer_id` int(11) NOT NULL,
  `task_type` enum('boarding','daycare','dog_walking') NOT NULL,
  `shift_date` date NOT NULL,
  `shift_time` time NOT NULL,
  `notes` text DEFAULT NULL,
  `status` enum('scheduled','completed','cancelled') DEFAULT 'scheduled'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `adoption_applications`
--
ALTER TABLE `adoption_applications`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `rescue_pet_id` (`pet_id`);

--
-- Indexes for table `center_capacity`
--
ALTER TABLE `center_capacity`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `category` (`category`);

--
-- Indexes for table `employees`
--
ALTER TABLE `employees`
  ADD PRIMARY KEY (`employee_id`),
  ADD UNIQUE KEY `user_id` (`user_id`);

--
-- Indexes for table `employee_roles`
--
ALTER TABLE `employee_roles`
  ADD PRIMARY KEY (`role_id`),
  ADD KEY `employee_id` (`employee_id`);

--
-- Indexes for table `other_services`
--
ALTER TABLE `other_services`
  ADD PRIMARY KEY (`service_id`);

--
-- Indexes for table `receipts`
--
ALTER TABLE `receipts`
  ADD PRIMARY KEY (`receipt_id`),
  ADD KEY `service_appointment_id` (`service_appointment_id`),
  ADD KEY `vet_appointment_id` (`vet_appointment_id`);

--
-- Indexes for table `rescue_pets`
--
ALTER TABLE `rescue_pets`
  ADD PRIMARY KEY (`pet_id`);

--
-- Indexes for table `service_appointments`
--
ALTER TABLE `service_appointments`
  ADD PRIMARY KEY (`appointment_id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `pet_id` (`pet_id`),
  ADD KEY `service_id` (`service_id`),
  ADD KEY `assigned_employee_id` (`assigned_employee_id`),
  ADD KEY `fk_appt_volunteer` (`assigned_volunteer_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Indexes for table `user_pets`
--
ALTER TABLE `user_pets`
  ADD PRIMARY KEY (`pet_id`),
  ADD KEY `user_pets_ibfk_1` (`user_id`);

--
-- Indexes for table `vet_appointments`
--
ALTER TABLE `vet_appointments`
  ADD PRIMARY KEY (`appointment_id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `pet_id` (`pet_id`),
  ADD KEY `service_id` (`service_id`),
  ADD KEY `assigned_vet` (`assigned_vet`);

--
-- Indexes for table `vet_services`
--
ALTER TABLE `vet_services`
  ADD PRIMARY KEY (`service_id`);

--
-- Indexes for table `volunteers`
--
ALTER TABLE `volunteers`
  ADD PRIMARY KEY (`volunteer_id`),
  ADD UNIQUE KEY `user_id` (`user_id`);

--
-- Indexes for table `volunteer_shifts`
--
ALTER TABLE `volunteer_shifts`
  ADD PRIMARY KEY (`shift_id`),
  ADD KEY `volunteer_id` (`volunteer_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `adoption_applications`
--
ALTER TABLE `adoption_applications`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `center_capacity`
--
ALTER TABLE `center_capacity`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `employees`
--
ALTER TABLE `employees`
  MODIFY `employee_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `employee_roles`
--
ALTER TABLE `employee_roles`
  MODIFY `role_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `other_services`
--
ALTER TABLE `other_services`
  MODIFY `service_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `receipts`
--
ALTER TABLE `receipts`
  MODIFY `receipt_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `rescue_pets`
--
ALTER TABLE `rescue_pets`
  MODIFY `pet_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- AUTO_INCREMENT for table `service_appointments`
--
ALTER TABLE `service_appointments`
  MODIFY `appointment_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `user_pets`
--
ALTER TABLE `user_pets`
  MODIFY `pet_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `vet_appointments`
--
ALTER TABLE `vet_appointments`
  MODIFY `appointment_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `vet_services`
--
ALTER TABLE `vet_services`
  MODIFY `service_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `volunteers`
--
ALTER TABLE `volunteers`
  MODIFY `volunteer_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `volunteer_shifts`
--
ALTER TABLE `volunteer_shifts`
  MODIFY `shift_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `adoption_applications`
--
ALTER TABLE `adoption_applications`
  ADD CONSTRAINT `adoption_applications_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `adoption_applications_ibfk_2` FOREIGN KEY (`pet_id`) REFERENCES `rescue_pets` (`pet_id`);

--
-- Constraints for table `employees`
--
ALTER TABLE `employees`
  ADD CONSTRAINT `employees_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Constraints for table `employee_roles`
--
ALTER TABLE `employee_roles`
  ADD CONSTRAINT `employee_roles_ibfk_1` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`employee_id`);

--
-- Constraints for table `receipts`
--
ALTER TABLE `receipts`
  ADD CONSTRAINT `receipts_ibfk_1` FOREIGN KEY (`service_appointment_id`) REFERENCES `service_appointments` (`appointment_id`),
  ADD CONSTRAINT `receipts_ibfk_2` FOREIGN KEY (`vet_appointment_id`) REFERENCES `vet_appointments` (`appointment_id`);

--
-- Constraints for table `service_appointments`
--
ALTER TABLE `service_appointments`
  ADD CONSTRAINT `fk_appt_volunteer` FOREIGN KEY (`assigned_volunteer_id`) REFERENCES `volunteers` (`volunteer_id`) ON DELETE SET NULL,
  ADD CONSTRAINT `service_appointments_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `service_appointments_ibfk_2` FOREIGN KEY (`pet_id`) REFERENCES `user_pets` (`pet_id`) ON DELETE SET NULL,
  ADD CONSTRAINT `service_appointments_ibfk_3` FOREIGN KEY (`service_id`) REFERENCES `other_services` (`service_id`),
  ADD CONSTRAINT `service_appointments_ibfk_4` FOREIGN KEY (`assigned_employee_id`) REFERENCES `employees` (`employee_id`) ON DELETE SET NULL;

--
-- Constraints for table `user_pets`
--
ALTER TABLE `user_pets`
  ADD CONSTRAINT `user_pets_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `vet_appointments`
--
ALTER TABLE `vet_appointments`
  ADD CONSTRAINT `vet_appointments_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `vet_appointments_ibfk_2` FOREIGN KEY (`pet_id`) REFERENCES `user_pets` (`pet_id`) ON DELETE SET NULL,
  ADD CONSTRAINT `vet_appointments_ibfk_3` FOREIGN KEY (`service_id`) REFERENCES `vet_services` (`service_id`),
  ADD CONSTRAINT `vet_appointments_ibfk_4` FOREIGN KEY (`assigned_vet`) REFERENCES `employees` (`employee_id`) ON DELETE SET NULL;

--
-- Constraints for table `volunteers`
--
ALTER TABLE `volunteers`
  ADD CONSTRAINT `volunteers_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Constraints for table `volunteer_shifts`
--
ALTER TABLE `volunteer_shifts`
  ADD CONSTRAINT `volunteer_shifts_ibfk_1` FOREIGN KEY (`volunteer_id`) REFERENCES `volunteers` (`volunteer_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
