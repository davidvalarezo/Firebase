<?php
namespace Proceso;

class Process 
{
    private $file = __DIR__.'/config.json';

    public function __construct(){
        $this->data = json_decode(file_get_contents($this->file));
    }

    //Envía la notificación a todos los usuarios de la APP Android/iOS
    public function sendAll($X){
		
        $msg = array(
                'title'        => $this->data->title,
                'body'         => $this->data->body,
                'icon'         => $this->data->icon,
                'sound'        => $this->data->sound,
                'click_action' => $X['actividad'],
                'color'        => $this->data->color
            );
        
        $data = array(
            "message"  => $X['mensaje'],//Mensaje a enviar desde el formulario
            "asunto"   => $X['asunto'],//Asunto del Mensaje desde el formulario
        );

        $fields = array(
                    'to'=> '/topics/'.$this->data->topic, 
                    'notification'	=> $msg,
                    'data'=>$data
                );
        $headers = array(
                    'Authorization: key=' . $this->data->apikey, 
                    'Content-Type: application/json'
                );


        $ch = curl_init();
        curl_setopt( $ch,CURLOPT_URL, 'https://fcm.googleapis.com/fcm/send' );
        curl_setopt( $ch,CURLOPT_POST, true );
        curl_setopt( $ch,CURLOPT_HTTPHEADER, $headers );
        curl_setopt( $ch,CURLOPT_RETURNTRANSFER, true );
        curl_setopt( $ch,CURLOPT_SSL_VERIFYPEER, false );
        curl_setopt( $ch,CURLOPT_POSTFIELDS, json_encode( $fields ) );
        $result = curl_exec($ch );
        curl_close( $ch );

        return (object) array('code'=>'200','data'=>$result);
    }//



    //Envía la notificación a un usuario especifico por su token
    public function sendUser($X){
        $msg = array(
                'title'        => $this->data->title,
                'body'         => $this->data->body,
                'icon'         => $this->data->icon,
                'sound'        => $this->data->sound,
                'click_action' => $X['actividad'],
                'color'        => $this->data->color
            );
        
        $data = array(
            "message"  => $X['mensaje'],//Mensaje a enviar desde el formulario
            "asunto"   => $X['asunto'],//Asunto del Mensaje desde el formulario
        );

        $fields = array(
                    'to'=> $X['token'], //Aqui se recibe el Token del usuario registrado
                    'notification'	=> $msg,
                    'data'=>$data
                );
        $headers = array(
                    'Authorization: key=' . $this->data->apikey, 
                    'Content-Type: application/json'
                );


        $ch = curl_init();
        curl_setopt( $ch,CURLOPT_URL, 'https://fcm.googleapis.com/fcm/send' );
        curl_setopt( $ch,CURLOPT_POST, true );
        curl_setopt( $ch,CURLOPT_HTTPHEADER, $headers );
        curl_setopt( $ch,CURLOPT_RETURNTRANSFER, true );
        curl_setopt( $ch,CURLOPT_SSL_VERIFYPEER, false );
        curl_setopt( $ch,CURLOPT_POSTFIELDS, json_encode( $fields ) );
        $result = curl_exec($ch );
        curl_close( $ch );

        return (object) array('code'=>'200','data'=>$result);
    }//
	
	//Envía la notificación a todos los usuarios de la APP Android/iOS
    public function sendGeneral($X){
		$host = 'localhost'; 
		$user = 'id12034473_puntuaciones'; 
		$pass = 'puntuaciones'; 
		$database = 'id12034473_puntuaciones_db'; 
		
		//Conexión a la base de datos 
		$conexion = mysqli_connect ($host, $user, $pass) or die 
		('Error al conectar con el servidor'.mysqli_error()); 
		mysqli_select_db($conexion, $database) or die 
		('->>Error seleccionando la base de datos'.mysqli_error()); 
		
		$result_query=mysqli_query($conexion, "SELECT * FROM dispositivos"); 
		while($row = mysqli_fetch_assoc ( $result_query )) { 
			//Recuperamos el id de registro del dispositivo en FCM 
			$deviceToken = $row['iddevice']; 
			//IMPORTANTE: Array con la información que enviará la 
			//notificación. 
		
			$msg = array(
					'title'        => $this->data->title,
					'body'         => $this->data->body,
					'icon'         => $this->data->icon,
					'sound'        => $this->data->sound,
					'click_action' => $X['actividad'],
					'color'        => $this->data->color
				);
			
			$data = array(
				"message"  => $X['mensaje'],//Mensaje a enviar desde el formulario
				"asunto"   => $X['asunto'],//Asunto del Mensaje desde el formulario
			);

			$fields = array(
						'to'=> $deviceToken,//'/topics/'.$this->data->topic, 
						'notification'	=> $msg,
						'data'=>$data
					);
			$headers = array(
						'Authorization: key=' . $this->data->apikey, 
						'Content-Type: application/json'
					);


			$ch = curl_init();
			curl_setopt( $ch,CURLOPT_URL, 'https://fcm.googleapis.com/fcm/send' );
			curl_setopt( $ch,CURLOPT_POST, true );
			curl_setopt( $ch,CURLOPT_HTTPHEADER, $headers );
			curl_setopt( $ch,CURLOPT_RETURNTRANSFER, true );
			curl_setopt( $ch,CURLOPT_SSL_VERIFYPEER, false );
			curl_setopt( $ch,CURLOPT_POSTFIELDS, json_encode( $fields ) );
			$result = curl_exec($ch );
			curl_close( $ch );
		};
        
			if($result_query != FALSE){ 
		    //header("Location: https://dvalarez.000webhostapp.com/eventos/envio_correcto.html");
			return (object) array('code'=>'200','data'=>$result);
            exit;
		}
    }//

}

?>