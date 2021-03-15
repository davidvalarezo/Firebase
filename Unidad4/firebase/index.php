<?php 
include('autoload.php');
use Proceso\Process;
$p = new Process();

$resp = "";

if(isset($_POST['tipo'])){

    switch ($_POST['tipo']) {
        case 'general':
            //$resp = $p->sendAll($_POST);
			$resp = $p->sendGeneral($_POST);
        break;
        
        case 'individual':
            //$resp = $p->sendUser($_POST);
			$resp = $p->sendGeneral($_POST);
        break;
    }
}

?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Formulario Firebase</title>
    <link rel="stylesheet" type="text/css" href="css/bootstrap.css">
    <link rel="stylesheet" type="text/css" href="css/toastr.min.css">
    <script src="js/jquery-3.2.1.min.js"></script>
    <script src="js/bootstrap.bundle.js"></script>
    <script src="js/toastr.min.js"></script>

<?php 
if($resp !=""){
    if($resp->code=="200"){
        //AQUI CODIGO DE ALERTA DE ENVIO EXITOSO

    }
}
?>

</head>
<body>

<div class="container">

<div class="alert alert-warning">
    Formulario de envío de notificaciones PUSH con Firebase
</div>

    
    <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
        <div class="row">
            <div class="col-xs-12 col-sm-12 col-md-12 col-lg-6">
                <form id="formulario" method="post" action="">
                <div class="row">
                    <div class="col-xs-12 col-sm-12 col-md-12 col-lg-6">
                        <div class="form-group">
                            <label>Tipo de Mensaje</label>
                            <select name="tipo" id="tipo" class="form-control" required="required">
                                <option value="">Seleccione</option>
                                <option value="general">General</option>
                                <option value="individual">Individual</option>
                            </select>
                        </div>
                    </div>
                    
                    <div class="col-xs-12 col-sm-12 col-md-12 col-lg-6">
                        <div class="form-group">
                            <label>Asunto del mensaje</label>
                            <input type="text" name="asunto" id="asunto" class="form-control" placeholder="Asunto" required />
                        </div>
                    </div>

                    
                    <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
                        <div class="form-group">
                            <label>Mensaje</label>
                            <textarea class="form-control" name="mensaje" id="mensaje" placeholder="Mensaje"></textarea>
                        </div>
                    </div>


                    <div class="col-xs-12 col-sm-12 col-md-12 col-lg-6">
                        <div class="form-group">
                            <label>Actividad que Recibe</label>
                            
                            <select name="actividad" id="actividad" class="form-control" required="required">
                                <option value="">Seleccione</option>
                                <?php foreach($p->data->actividades as $item){ ?>
                                <option value="<?php echo $item->nombre ?>"><?php echo $item->etiqueta ?></option>
                                <?php } ?>
                            </select>
                            
                        </div>
                    </div>
                    

                    
                    <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
                        <div class="form-group">
                            <center>
                                <button type="submit" class="btn btn-primary">Enviar</button>
                            </center>
                        </div>
                    </div>
                    
                    

                </div>
                </form>
                
                
            </div>
            <div class="col-xs-12 col-sm-12 col-md-12 col-lg-6">
                
                <div class="alert alert-info">
                    <p>Mediante este formulario, podrá enviar notificaciones push a los usuarios de su Aplicación Android e iOS, para iniciar su uso debe configurar algunos parámetros del archivo config.json</p>

                    <p>Primeramente debe indicar el <b>topic</b> es cual es el canal general al cual se registran sus usuarios al momento de instalar la app e iniciarla, este canal biene por defecto como <code>general</code> pero puede cambiarlo por el nombre de su canal creado en Firebase.</p>
                    
                    <p>Tambien debe configurar el <b>apikey</b> para que el formulario se pueda enlazar a su proyecto de firebase console, esta <code>apikey</code> la obtiene en el panel de administración de su proyecto firebase.</p>

                    <p>El <b>icon</b> y el <b>color</b> son los elementos que se mostrarán en la notificación al llegar, el <code>icon</code> es el nombre del icono que se encuentra compilado en su app para las notificaciones y el <code>color</code> es color de fondo del icono de la notificación.</p>

                    <p>Luego tenemos un arreglo llamado <b>actividades</b>, alli se configuran las distintas activities configuradas en su app para recibir las notificaciones PUSH, la <code>etiqueta</code> es para ser visualizada en el selector del formulario mientras que el <code>nombre</code> es el nombre de la activity registrada en su app que se abrira al darle clic a la notificación</p>

                    <p>
                        <ul>
                            <li>Autor: <code><?php echo $p->data->autor->name ?></code></li>
                            <li>Email: <code><?php echo $p->data->autor->email ?></code></li>
                            <li>Empresa: <code><?php echo $p->data->autor->company ?></code></li>
                        </ul>
                    </p>

                </div>
                
            </div>
        </div>
        
    </div>
    
</div>


</body>
</html>