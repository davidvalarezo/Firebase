![Banner Firebase](http://www.imaginaformacion.com/wp-content/uploads/2017/12/firebase-banner.png "Firebase Cloud Messagin Form")

# Form for sending notifications with php through [Firebase Cloud Messagin](https://console.firebase.google.com) #

_This basic form can be used both in local mode with XAMP, WAMP, LARAGON, etc., as in a remote server, hosting, vps, etc._

_For its configuration, it has a JSON file in the root of the project called config.json, in which you can configure some parameters of the form as shown in the following block of code_


`{
    "title":"Here the Title",
    "body":"Here the body of the notification",
    "icon":"ic_notifi_icon",
    "sound":"defaultSoundUri",
    "color":"#3F51B5",
    "topic":"general",
    "apikey":"HERE FIREBASE MESSAGE API",
    "autor":{
        "name":"Carlos Quintero",
        "email":"info.fxstudios@gmail.com",
        "company":"Corporación HITCEL"
    },
    "actividades":[
        {
            "etiqueta":"Promoción",
            "nombre":"PromocionActivity"
        },
        {
            "etiqueta":"Simple",
            "nombre":"SimpleActivity"
        },
        {
            "etiqueta":"Imagen",
            "nombre":"ImagenActivity"
        }
    ]
}
`

_the activity node, serves to indicate to which activity of your app the notification will arrive or which screen should be opened when you click on it, and that will depend on how your app is scheduled_

_The topic node which has the general value by default, is where they configure their general channel of notifications in firebase to which users register automatically when registering the app on their devices, in the same way the form has 2 options of sending , one generally and the other individually (not configured and must be customized by you), in this second mode you can send notifications to specific users of your app as payment notifications for example._

_This is a basic form and can be cloned and modified to your actual communication needs_