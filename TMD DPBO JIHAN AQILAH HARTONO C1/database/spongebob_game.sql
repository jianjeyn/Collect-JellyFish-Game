-- phpMyAdmin SQL Dump
-- version 5.2.2
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jun 20, 2025 at 03:24 PM
-- Server version: 8.0.30
-- PHP Version: 8.1.10

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `spongebob_game`
--

-- --------------------------------------------------------

--
-- Table structure for table `thasil`
--

CREATE TABLE `thasil` (
  `id` int NOT NULL,
  `username` varchar(50) NOT NULL,
  `skor` int DEFAULT '0',
  `count` int DEFAULT '0',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Dumping data for table `thasil`
--

INSERT INTO `thasil` (`id`, `username`, `skor`, `count`, `created_at`) VALUES
(1, 'Manusia', 1000, 100, '2025-06-03 09:18:00'),
(2, 'BukanManusia', 800, 80, '2025-06-03 09:18:00'),
(3, 'Barbie', 700, 40, '2025-06-03 09:18:00'),
(4, 'jen', 11000, 444, '2025-06-03 11:17:17'),
(5, 'jihan', 770, 35, '2025-06-06 14:40:23'),
(6, 'test', 640, 12, '2025-06-20 09:04:22'),
(7, 'test2', 530, 27, '2025-06-20 09:16:39'),
(8, 'test3', 1130, 25, '2025-06-20 09:25:43');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `thasil`
--
ALTER TABLE `thasil`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `thasil`
--
ALTER TABLE `thasil`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
