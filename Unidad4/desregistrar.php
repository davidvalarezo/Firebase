<?php 
	//Conexión a la base de datos 
	$conexion = mysqli_connect('localhost', 'id12034473_puntuaciones', 'puntuaciones') or die 
		(mysqli_error()); 
	mysqli_select_db($conexion, 'id12034473_puntuaciones_db'); 
	//Eliminamos el dispositivo basándonos en el id de registro del FCM. 
	$sql = "DELETE FROM dispositivos WHERE 
		iddevice='".$_POST["iddevice"]."'"; 
	mysqli_query($conexion, $sql) or die(mysqli_error()); 
	mysqli_close(); 
?>