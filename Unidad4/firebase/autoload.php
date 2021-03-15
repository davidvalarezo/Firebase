<?php
spl_autoload_register(function ($clase) {
    $parts = explode('\\', $clase);
    require end($parts) . '.php';
});
?>