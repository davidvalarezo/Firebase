<?php 
	//Conexión a la base de datos 
	$conexion = mysqli_connect('localhost', 'id12034473_puntuaciones', 'puntuaciones') or die 
	(mysqli_error()); 
	mysqli_select_db($conexion, 'id12034473_puntuaciones_db'); 
//Insertamos id de registro devuelto por el FCM. 
	mysqli_query($conexion, "INSERT INTO dispositivos (iddevice) VALUES 
	('".$_POST["iddevice"]."')") or die(mysqli_error()); 
	mysqli_close(); 
?>