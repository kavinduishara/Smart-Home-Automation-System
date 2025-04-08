const firebaseConfig = {
    apiKey: "AIzaSyBgVf7JwwkTGyD2tGFMya7PzPTJQXcNBwY",
    authDomain: "home-automation-de3c9.firebaseapp.com",
    databaseURL: "https://home-automation-de3c9-default-rtdb.firebaseio.com/",
    projectId: "home-automation-de3c9",
    storageBucket: "home-automation-de3c9.appspot.com",
    messagingSenderId: "1234567890",
    appId: "1:1234567890:web:abcdefghij",
  };

  firebase.initializeApp(firebaseConfig);
  const auth = firebase.auth();
  const db = firebase.database();
  const uid = localStorage.getItem("uid");

  document.getElementById("add").addEventListener("click", function () {
    const name = document.getElementById("input").value.trim();
    const pin = document.getElementById("pininput").value.trim();

    if (name && pin) {
      const pinRef = db.ref(`users/${uid}/lights/${pin}`);
      pinRef
        .set({ name: name, state: false })
        .then(() => {
          console.log(`Light ${name} added at pin ${pin}.`);
          document.getElementById("input").value = "";
          document.getElementById("pininput").value = "";
          location.reload(); // reload to update button
        })
        .catch((error) => {
          console.error("Error adding light:", error);
        });
    } else {
      alert("Please enter both a light name and a pin number.");
    }
  });

  if (uid) {
    const lightsContainer = document.getElementById("lights-container");
    db.ref(`users/${uid}/lights`)
      .once("value")
      .then((snapshot) => {
        const lights = snapshot.val() || {};
        for (let pin in lights) {
          const light = lights[pin];
          const name = light.name;
          const state = light.state;

          const button = document.createElement("button");
          button.textContent = `${name}: ${state ? "ON" : "OFF"}`;
          button.classList.add("light-button", state ? "on" : "off");
          button.setAttribute("data-pin", pin);
          button.setAttribute("data-name", name);

          button.addEventListener("click", function () {
            const newState = !light.state;
            db.ref(`users/${uid}/lights/${pin}/state`)
              .set(newState)
              .then(() => {
                console.log(`Pin ${pin} (${name}) updated to ${newState ? "ON" : "OFF"}`);
                button.textContent = `${name}: ${newState ? "ON" : "OFF"}`;
                button.classList.toggle("on", newState);
                button.classList.toggle("off", !newState);
                light.state = newState;
              })
              .catch((error) => {
                console.error("Error updating light:", error);
              });
          });

          lightsContainer.appendChild(button);
        }
      })
      .catch((error) => {
        console.error("Error loading lights:", error.message);
      });
  } else {
    alert("No user is signed in. Please sign in first.");
  }

  function handleGateControl() {
    const gateRef = db.ref(`users/${uid}/door`);
    gateRef
      .once("value")
      .then((snapshot) => {
        const doorData = snapshot.val() || {};
        const currentState = doorData.state || false;

        if (!currentState) {
          const passValue = document.getElementById("gatepass").value;
          if (passValue.trim() === "") {
            alert("Please enter gate password.");
            return;
          }

          gateRef
            .update({ clientpass: passValue, state: false })
            .then(() => {
              console.log("Password sent to clientpass.");
              alert("Password sent. Waiting for unlock...");
            })
            .catch((error) => {
              console.error("Error sending password:", error);
            });
        } else {
          db.ref(`users/${uid}/door/state`)
            .set(false)
            .then(() => {
              console.log("Gate locked.");
              alert("Gate has been locked.");
            })
            .catch((error) => {
              console.error("Error locking gate:", error);
            });
        }
      })
      .catch((error) => {
        console.error("Error reading gate state:", error);
      });
  }

  document.getElementById("gate").addEventListener("click", handleGateControl);

  db.ref(`users/${uid}/door/state`).on("value", (snapshot) => {
    const state = snapshot.val();
    const gateBtn = document.getElementById("gate");
    const gateInput = document.getElementById("gatepass");

    if (state) {
      gateBtn.textContent = "Lock gate";
      gateInput.disabled = true;
      speakResponse('gate unloked')
    } else {
      gateBtn.textContent = "Unlock gate";
      gateInput.disabled = false;
      speakResponse('gate locked')
    }
  });
  initVoice = () => {
      const voices = speechSynthesis.getVoices();
      selectedVoice = voices.find(voice => voice.lang === 'en-US') || voices[1];
  };
  speakResponse = (response) => {
      const utterance = new SpeechSynthesisUtterance(response);
      utterance.voice = selectedVoice;
      utterance.rate = 1.0;
      utterance.pitch = 1.0;
      speechSynthesis.speak(utterance);
  };
  

  // Initialize Messaging
  const messaging = firebase.messaging();
  
  // Request permission
  Notification.requestPermission().then(() => {
    return messaging.getToken({ vapidKey: "YOUR_VAPID_KEY_HERE" });
  }).then(token => {
    console.log("FCM Token:", token);
    // Save token in your database under user's UID
    const uid = localStorage.getItem("uid");
    if (uid) {
      firebase.database().ref(`users/${uid}/fcmToken`).set(token);
    }
  }).catch(err => {
    console.error("FCM error:", err);
  });
  
  // Listen for foreground messages
  messaging.onMessage((payload) => {
    console.log("Message received. ", payload);
    alert(`Notification: ${payload.notification.title} - ${payload.notification.body}`);
  });
  