<?php
$servername = "fdb20.awardspace.net";
$username = "3822958_games";
$password = "ExLA#2JK2-(09kK8";

// Create connection
$conn = new mysqli($servername, $username, $password);

// Check connection
if ($conn->connect_error) {
  die("Connection failed: " . $conn->connect_error);
}
echo "Connected successfully";
?>