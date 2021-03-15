<!DOCTYPE html>

<html lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta charset="utf-8" />
    <title>Notificaciones</title>
	<link href="styles.css" rel="stylesheet" type="text/css">
	<link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
</head>
<body>
	<header>
        <h1>Servidor Messaging</h1>
    </header>
    <nav id="navbar">
        <ul>
            <li>
                <a href="notificar.html"><i class="material-icons">mail_outline</i><span>Enviar Nofiticación</span></a>
            </li>
            <li>
                <a href="listar.html"><i class="material-icons">reorder</i><span>Listar Dispositivos</span></a>
            </li>
        </ul>
    </nav>
	<div id="cabecera">
      <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
          <td width="20%" align="center" valign="middle"><img src="http://www.androidcurso.com/images/certificado_upv.jpg"></td>
          <td width="80%" align="left" valign="middle">Enviar Notificaciones a Dispositivos - Servidor Valarezo!</td>
        </tr>
      </table>
    </div>
	<div id="contenido">
		<p><a href="notificar.html" data-role="button">Volver a Enviar Notificación</a>
		<p>Dispositivos registrados</p>
		<table border="1" cellspacing=1 cellpadding=2 style="font-size: 8pt"><tr>
			<td><font face="verdana"><b>No</b></font></td>
			<td><font face="verdana" align="center"><b>Identificador del dispositivo</b></font></td>
		</tr>
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
			$result=mysqli_query($conexion, "SELECT * FROM dispositivos"); 
			$numero = 0;
			  while($row = mysqli_fetch_array($result))
			  {
				if($numero != 0){
					echo "<tr><td width=\"10%\"><font face=\"verdana\">" . 
					$numero . "</font></td>";
					echo "<td width=\"100%\"><font face=\"verdana\">" . 
					$row["iddevice"] . "</font></td>";  
				}	 
				$numero++;
			  }
			  echo "<tr><td colspan=\"15\"><font face=\"verdana\"><b>Número: " . --$numero . 
				  "</b></font></td></tr>";
		  
			  mysqli_free_result($result);
			  mysqli_close($conexion);
		?>
		</table>
	</div>
</body>
</html>