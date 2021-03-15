<?php 
	$host = 'localhost'; 
	$user = 'id12034473_puntuaciones'; 
	$pass = 'puntuaciones'; 
	$database = 'id12034473_puntuaciones_db'; 
	
	//Conexión a la base de datos 
	$conexion = mysqli_connect ($host, $user, $pass) or die 
	('Error al conectar con el servidor'.mysqli_error()); 
	mysqli_select_db($conexion, $database) or die 
	('->>Error seleccionando la base de datos'.mysqli_error()); 
	
	if ( $_POST['mensaje'] != "") { 
		$message =$_POST['mensaje'];
		//Título de la notificación	
		$title = 'Notificación'; 
		if ( !empty($_POST['title']) ) { 
		$title = $_POST['title'];
		}
		//Evento
		$evento = '';
		if ( !empty($_POST['evento']) ) { 
		$evento = $_POST['evento'];
		}
		//click_action
		$click_action = 'OPEN_ACTIVITY_1';
		if ( !empty($_POST['click_action']) ) { 
		$click_action = $_POST['click_action'];
		}		
		//Cambiar Token de Firebase Cloud Messaging 
		$apiKey = 'AAAA5PkKxrw:APA91bFCFU2qd-Cde78CFLGPyGx4lptsPruGlzbvOY-wToJqckHAV0b9nf5inQ0rNlmznJ6uRZeLN-mZBbZJOW_5rLuDVMUt7__hq0KuPNy0StZFBSOjZPTG1nF2rR5a-2_Mo306Bois';		
		if ( !empty($_POST['apiKey']) ) { 
			$apiKey = $_POST['apiKey'];
		}
		
		$result=mysqli_query($conexion, "SELECT * FROM dispositivos"); 
		while($row = mysqli_fetch_assoc ( $result )) { 
			//Recuperamos el id de registro del dispositivo en FCM 
			$deviceToken = $row['iddevice']; 
			//IMPORTANTE: Array con la información que enviará la 
			//notificación. 
			$data = array( 
				'to' => $deviceToken, 
				'collapse_key' => 'col_key',
				'notification' => array(
					'title'=> $title,
					'evento'=> $evento,					
					'body' => $message,
					'click_action' => $click_action,
					'color' => '#f45342',
					'vibrate'   => 1,
					'evento'=> $evento,
					'sound' => 'default', 
					'badge' => '1')
			); 
			
			//Código para conectar con FCM y enviar notificación. 
			// No modificar. 
			$ch = curl_init(); 
			curl_setopt($ch, CURLOPT_URL, 
				"https://fcm.googleapis.com/fcm/send"); 
			$headers = array('Authorization:key=' . $apiKey, 'Content-Type: application/json'); 
			$data=json_encode($data); 
			curl_setopt($ch, CURLOPT_HTTPHEADER, $headers); 
			curl_setopt($ch, CURLOPT_POST, true); 
			curl_setopt($ch, CURLOPT_RETURNTRANSFER, true); 
			curl_setopt($ch, CURLOPT_POSTFIELDS, $data); 
			$resultado = curl_exec($ch); 
			curl_close($ch); 
		}; 
		if($result != FALSE){ 
		    header("Location: https://dvalarez.000webhostapp.com/eventos/envio_correcto.html");
            exit;
		}
			
	} 
?>