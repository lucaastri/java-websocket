// https://it.javascript.info/websocket

let socket = new WebSocket("ws://localhost:8086");
var username;
const passwordAdmin = "cisco";

function logMessageSend(message) {

  var txtLog = document.getElementById("txtLog");
  var log = document.createElement("p");
  log.innerHTML = "[" + (new Date()).toLocaleString() + "] " + "Messaggio inviato: " + message;
  txtLog.append(log);

};

function logMessageRecieve(message) {
  var txtLog = document.getElementById("txtLog");
  var log = document.createElement("p");
  log.className = "input";
  //hover sul log? pronto da implementare (forse)

  try {
    var data = JSON.parse(message);

    // prendi solo il campo "field"
    var cleanMessage = data.field || message;
    //var cleanMessage = data.field || message => se data.field non è nulla (null,0,undefined,""),
    //assegna data.field, altrimenti assegna msg

  } catch (e) {
    var cleanMessage = message;
  }

  log.innerHTML = "[" + (new Date()).toLocaleString() + "] " + "Messaggio ricevuto: " + cleanMessage;
  txtLog.append(log);
};

function group(event) {
  var to = prompt("inserisci i nomi delle persone da aggiungere al gruppo, separati da una virgola: ");
  var name = prompt("inserisci il nome del gruppo:")
  var msg = document.getElementById("txtMessage");
  var message = msg.value + to;
  var msgObject = {
    "request":"GROUP",
    "to":name,
    "message":msg 
  };
  var txt = JSON.stringify(msgObject);
  logMessageSend(txt);
  console.log(txt);
  send(txt);
};

function setAdmin(event) {
  var passw = prompt("password: ");
  if (passw == passwordAdmin) {
    //list(event);
    var disconnect = prompt("disconnetti: ");
      var msgObject = {
    "request":"END",
    "to":disconnect
    };
    var txt = JSON.stringify(msgObject);
    logMessageSend(txt);
    console.log(txt);
    send(txt);
    /*setTimeout(() => {
      console.log("chiudo la connessione a " + disconnect);
      socket.close();
    }, 1300);*/
  }
};


function invia(event) {
  var msg = document.getElementById("txtMessage");
  var to = prompt("A chi vuoi scrivere: ");
  var msgObject = {
    "request":"SEND",
    "to":to,
    "message":msg.value
  };
  var txt = JSON.stringify(msgObject);

  logMessageSend(txt);
  console.log(txt);
  send(txt);

};

function list(event) {
  var msgObject = {
    "request":"LIST"
  };
  var txt = JSON.stringify(msgObject);
  logMessageSend(txt);
  console.log(txt);
  send(txt);
};


function send(data) {
  console.log("messaggio inviato: " + data);
  waitForConnection(function () {
    socket.send(data);
  }, 1000);
};

function broad(event) {
  var msg = document.getElementById("txtMessage");
  var msgObject = {
    "request":"SEND",
    "to":"BROAD",
    "message":msg.value
  };
  var txt = JSON.stringify(msgObject);
  logMessageSend(txt);
  console.log(txt);
  send(txt);
};

function chiudi(event) {
    //console.log("voglio chiudere la connessione?");
  var msgObject = {
    "request":"END",
    "to":username
  };
  var txt = JSON.stringify(msgObject);
  logMessageSend(txt);
  console.log(txt);
  send(txt);

  setTimeout(() => {
    console.log("chiudo la connessione!");
    socket.close();
  }, 1300);
};

function waitForConnection(callback, delay) {
  if (socket.readyState === 1) {
    callback();
  } else {
      // optional: implement backoff for interval here
      setTimeout(function () {
          waitForConnection(callback, delay);
      }, delay);
  } 

};

function authenticate(username) {
  var msgObject = {
    "request":"AUTH",
    "to":username
  }
  var txt = JSON.stringify(msgObject);
  logMessageSend(txt);
  console.log(txt);
  send(txt);
};

document.onload = function createWebSocket() {
};
  socket.onopen = function (e) {
    alert("[open] Connessione stabilita");
    username = prompt("Scrivi il tuo username: ");
    authenticate(username);
    console.log("Stato socket:", socket.readyState);
  };

  socket.onmessage = function (event) {
    console.log(`[message] Ricezione dati dal server: ${event.data}`);
    logMessageRecieve(event.data);

  };

  socket.onclose = function (event) {
    if (event.wasClean) {
      alert(`[close] Connessione chiusa con successo, code=${event.code} reason=${event.reason}`);
    } else {
      // e.g. processo del server terminato o connessione già
      // in questo caso event.code solitamente è 1006
      alert('[close] Connection morta.');
    }
  };

  socket.onerror = function (error) {
    alert(`[error] ${error.message}`);
  };

//};