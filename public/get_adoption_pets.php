<?php
header('Content-Type: application/json');

// Execute Java RescuePetDao via MainController
$cmd = 'java -cp "../bin;../lib/*" com.pawcare.MainController getRescuePets';
$output = shell_exec($cmd);

echo $output;
?>