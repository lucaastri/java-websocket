<?php 
session_start();
?>
<!DOCTYPE html>
<html lang="it">
    <head>
        <title>Web socket client</title>
        <link rel="stylesheet" href="./css/style.css">
    </head>
    <body>
        <h1>Web socket client</h1>
        <div class="container form">
            <h2>Chat del client</h2>
            <label>Messaggio</label>
            <input id="txtMessage" type="text" name="message"></div>
            <button id ="closeBtn" class="button end" onclick="chiudi(event)">End</button>
            <button class="button send" onclick="invia(event)">Invia</button>
            <button class="button list" onclick="list(event)">Lista</button>
            <button class="button broad" onclick="broad(event)">Broadcast</button>
            <button class="button admin" onclick="setAdmin(event)">Admin</button>
        </div>
        <div>
            <h2>messaggi</h2>
            <div id="txtLog" class="container log">

            </div>
        </div>
        <div>
            <h5> Note: </h5> 
            <p> - per scrivere ad un gruppo, dopo averlo creato, inserire il nome nel prompt del messaggio! <p>
        </div>
    </body>
<script src="./javascript/main.js"> </script>
    
</html>