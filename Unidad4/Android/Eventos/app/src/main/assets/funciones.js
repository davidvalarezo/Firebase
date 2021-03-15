// JavaScript source code
var evento = ""; var wiki = "";
function muestraEvento(mEvento) {
    switch (mEvento) {
        case "carnaval":
            evento = "Carnaval";
            wiki = "El carnaval es una celebraci&oacute;n que tiene lugar inmediatamente antes del inicio de la cuaresma cristiana, que se inicia a su vez con el Mi&eacute;rcoles de Ceniza, que tiene fecha variable (entre febrero y marzo seg&uacute;n el a&ntilde;o). El carnaval combina algunos elementos como disfraces, desfiles, y fiestas en la calle.";
            break;
        case "fallas":
            evento = "Fallas";
            wiki = "Las Fallas (Falles en valenciano) son unas fiestas que van del 15 al 19 de marzo con una tradici&oacute;n arraigada en la ciudad de Valencia y diferentes poblaciones de la Comunidad Valenciana. Oficialmente empiezan el &uacute;ltimo domingo de febrero con el acto de la Crida.";
            break;
        case "nochevieja":
            evento = "Nochevieja";
            wiki = "La Nochevieja, v&iacute;spera de A&ntilde;o Nuevo, A&ntilde;o Viejo o fin de a&ntilde;o, es la &uacute;ltima noche del a&ntilde;o en el calendario gregoriano, comprendiendo desde 31 de diciembre hasta el 1 de enero (A&ntilde;o Nuevo). Desde que se cambi&oacute; al calendario gregoriano en el a&ntilde;o 1582, se suele celebrar esta festividad, aunque ha ido evolucionando en sus costumbres y supersticiones.";
            break;
        case "sanjuan":
            evento = "Noche de San Juan";
            wiki = "La v&iacute;spera de San Juan o noche de San Juan es una festividad cristiana, de origen pagano (Litha) celebrada el 23 de junio,1 en la que se suelen encender hogueras o fuegos y ligada con las celebraciones en las que se festejaba la llegada del solsticio de verano, el 21 de junio en el hemisferio norte, cuyo rito principal consiste en encender una hoguera.";
            break;
        case "semanasanta":
            evento = "Semana Santa";
            wiki = "La Semana Santa es la conmemoraci&oacute;n anual cristiana de la Pasi&oacute;n, Muerte y Resurrecci&oacute;n de Jes&uacute;s de Nazaret. Por eso, es un per&iacute;odo de intensa actividad lit&uacute;rgica dentro de las diversas confesiones cristianas. Da comienzo el Domingo de Ramos y finaliza el Domingo de Resurrecci&oacute;n,1 aunque su celebraci&oacute;n suele iniciarse en varios lugares el viernes anterior (Viernes de Dolores) y se considera parte de la misma el Domingo de Resurrecci&oacute;n.";
            break;
        default:
            wiki = "No se encuentra el evento";
    }
    muestra(evento, wiki);
}

function muestra(mEvento, mWiki) {
    document.getElementById("evento").innerHTML = mEvento;
    document.getElementById("wiki").innerHTML = mWiki;
}