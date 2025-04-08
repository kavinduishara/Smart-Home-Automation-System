#include <WiFi.h>
#include <FirebaseESP32.h>
#include <ESP32Servo.h>
#include <Keypad.h>

// Wi-Fi credentials
#define WIFI_SSID "gl76"
#define WIFI_PASSWORD "11111111"

// Firebase credentials
#define DATABASE_URL "https://home-automation-de3c9-default-rtdb.firebaseio.com/"
#define DATABASE_SECRET "THXEsTlGLO9neBxtzUknhy7rTnFuJE47DbJm9Qbx"

// Firebase objects
FirebaseData fbdo;
FirebaseAuth auth;
FirebaseConfig config;

// UID and pins
String uid = "stzvqyARVXMcJhSnhHieQenmSUt1";
int lightPins[] = {2, 16, 17};

// Sensor pins and states
const int NUM_SENSORS = 4;
const int sensorPins[NUM_SENSORS] = {21, 16, 17, 22}; // D21, D16, D17, D22
int lastSensorStates[NUM_SENSORS] = {LOW, LOW, LOW, LOW}; // Track states for all sensors
String sensorNames[NUM_SENSORS] = {"motion_sensor", "door_sensor", "window_sensor", "smoke_sensor"};

// Servo Motor & Buzzer
Servo doorLock;
const int servoPin = 15;
const int buzzerPin = 4;

// Keypad Configuration
const byte ROWS = 4, COLS = 4;
char keys[ROWS][COLS] = {
  {'1','2','3','A'},
  {'4','5','6','B'},
  {'7','8','9','C'},
  {'*','0','#','D'}
};
byte rowPins[ROWS] = {32, 33, 25, 26};
byte colPins[COLS] = {27, 14, 12, 13};
Keypad keypad = Keypad(makeKeymap(keys), rowPins, colPins, ROWS, COLS);

// Password handling
String correctPassword = "1234";
String inputPassword = "";

// Sensor Data
#define Vresistor A0
int Vrdata = 0;

// Timing variables for non-blocking operations
unsigned long lastSensorUpdate = 0;
const unsigned long sensorUpdateInterval = 2000; // Update sensor every 2 seconds

void setup() {
  Serial.begin(115200);
  Serial.println("\n\n--- ESP32 Home Automation System Starting ---");

  // Pin setup
  pinMode(buzzerPin, OUTPUT);
  doorLock.attach(servoPin, 500, 2500);
  doorLock.write(0); // Initially locked
  Serial.println("Door locked on startup");

  // Initialize light pins (might overlap with sensor pins - handled in the code)
  for (int i = 0; i < sizeof(lightPins) / sizeof(lightPins[0]); i++) {
    pinMode(lightPins[i], OUTPUT);
    digitalWrite(lightPins[i], LOW);
    Serial.println("Light pin " + String(lightPins[i]) + " initialized");
  }
  
  // Initialize all sensor pins
  for (int i = 0; i < NUM_SENSORS; i++) {
    pinMode(sensorPins[i], INPUT);
    Serial.println(sensorNames[i] + " on pin " + String(sensorPins[i]) + " initialized");
  }

  // Wi-Fi connection
  connectToWiFi();

  // Firebase configuration
  configureFirebase();
  
  // Sensor setup
  pinMode(Vresistor, INPUT);
  Serial.println("Analog sensor initialized");
  
  // Initial database values setup
  initializeDatabaseValues();
}

void connectToWiFi() {
  Serial.print("Connecting to Wi-Fi...");
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  
  unsigned long wifiStartTime = millis();
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(300);
    // Add timeout for WiFi connection
    if (millis() - wifiStartTime > 30000) {
      Serial.println("\nWiFi connection timed out. Restarting...");
      ESP.restart();
    }
  }
  
  Serial.println("\nConnected with IP: " + WiFi.localIP().toString());
}

void configureFirebase() {
  Serial.println("Configuring Firebase connection...");
  
  // Firebase configuration for FirebaseESP32 library
  config.database_url = DATABASE_URL;
  config.signer.tokens.legacy_token = DATABASE_SECRET;
  
  Firebase.reconnectWiFi(true);
  Firebase.begin(&config, &auth);
  
  // Set database read timeout to 1 minute (60s)
  Firebase.setReadTimeout(fbdo, 1000 * 60);
  // Size and its write timeout e.g. tiny (1s), small (10s), medium (30s) and large (60s).
  Firebase.setwriteSizeLimit(fbdo, "tiny");
  
  // Setup Firebase stream
  setupFirebaseStream();
}

void setupFirebaseStream() {
  String path = "/users/" + uid;
  Serial.println("Setting up Firebase stream at path: " + path);
  
  if (!Firebase.beginStream(fbdo, path)) {
    Serial.println("FAILED TO BEGIN STREAM:");
    Serial.println(fbdo.errorReason());
  } else {
    Serial.println("Stream started successfully");
  }
}

void initializeDatabaseValues() {
  // Set initial values in the database for synchronization
  Serial.println("Initializing database values...");
  
  // Initialize door state
  String doorStatePath = "/users/" + uid + "/door/state";
  if (Firebase.setBool(fbdo, doorStatePath, false)) {
    Serial.println("Door state initialized as locked");
  } else {
    Serial.println("Failed to initialize door state: " + fbdo.errorReason());
  }
  
  // Initialize lights
  for (int i = 0; i < sizeof(lightPins) / sizeof(lightPins[0]); i++) {
    String lightPath = "/users/" + uid + "/lights/" + String(lightPins[i]) + "/state";
    if (Firebase.setBool(fbdo, lightPath, false)) {
      Serial.println("Light " + String(lightPins[i]) + " initialized as OFF");
    } else {
      Serial.println("Failed to initialize light " + String(lightPins[i]) + ": " + fbdo.errorReason());
    }
  }
  
  // Initialize sensors
  for (int i = 0; i < NUM_SENSORS; i++) {
    String sensorPath = "/users/" + uid + "/sensors/" + sensorNames[i] + "_alert";
    if (Firebase.setBool(fbdo, sensorPath, false)) {
      Serial.println(sensorNames[i] + " alert initialized as false");
    } else {
      Serial.println("Failed to initialize " + sensorNames[i] + " alert: " + fbdo.errorReason());
    }
  }
  
  // Initialize sensor data
  updateSensorData();
}

void loop() {
  unsigned long currentMillis = millis();
  
  // Handle keypad input
  char key = keypad.getKey();
  if (key) {
    handleKeypadInput(key);
  }
  
  // Process any changes from Firebase
  if (Firebase.ready()) {
    Firebase.readStream(fbdo);
    processStreamData(fbdo);
  }
  
  // Read and send sensor data at regular intervals
  if (currentMillis - lastSensorUpdate >= sensorUpdateInterval) {
    lastSensorUpdate = currentMillis;
    updateSensorData();
  }
}

void handleKeypadInput(char key) {
  Serial.print("Key Pressed: ");
  Serial.println(key);

  if (key == '#') {
    Serial.print("Entered Password: ");
    Serial.println(inputPassword);
    validateAndUnlock(inputPassword);
    inputPassword = "";
  } else if (key == '*') {
    inputPassword = "";
    Serial.println("Password cleared.");
  } else if (key == 'C') {
    lockDoor();
  } else {
    inputPassword += key;
  }
}

void updateSensorData() {
  // Check all sensors
  for (int i = 0; i < NUM_SENSORS; i++) {
    // Skip pins that are used as outputs for lights
    bool isLightPin = false;
    for (int j = 0; j < sizeof(lightPins) / sizeof(lightPins[0]); j++) {
      if (sensorPins[i] == lightPins[j]) {
        // This pin is used as a light output, so we'll skip sensor reading
        isLightPin = true;
        break;
      }
    }
    
    if (!isLightPin) {
      // Read current state of this sensor pin
      int currentSensorState = digitalRead(sensorPins[i]);
      
      // Check if sensor state changed to HIGH
      if (currentSensorState == HIGH && lastSensorStates[i] == LOW) {
        Serial.println(sensorNames[i] + " on pin " + String(sensorPins[i]) + " triggered (HIGH)!");
        
        // Send a signal to Firebase when sensor goes HIGH
        String sensorAlertPath = "/users/" + uid + "/sensors/" + sensorNames[i] + "_alert";
        if (Firebase.setBool(fbdo, sensorAlertPath, true)) {
          Serial.println(sensorNames[i] + " alert sent successfully");
        } else {
          Serial.print("Failed to send " + sensorNames[i] + " alert: ");
          Serial.println(fbdo.errorReason());
        }
      }
      
      // Update last state
      lastSensorStates[i] = currentSensorState;
    }
  }
}

void lockDoor() {
  doorLock.write(0);  // 0 degrees = locked position
  Serial.println("Gate locked by keypad.");
  
  String statePath = "/users/" + uid + "/door/state";
  if (Firebase.setBool(fbdo, statePath, false)) {
    Serial.println("Door state updated to false (locked).");
  } else {
    Serial.print("Failed to update door state: ");
    Serial.println(fbdo.errorReason());
  }
  
  // Clear the client password after locking the door
  String passPath = "/users/" + uid + "/door/clientpass";
  if (Firebase.setString(fbdo, passPath, "null")) {
    Serial.println("Client password cleared.");
  } else {
    Serial.print("Failed to clear client password: ");
    Serial.println(fbdo.errorReason());
  }
}

void validateAndUnlock(String clientpass) {
  if (clientpass == correctPassword) {
    Serial.println("Correct password. Unlocking door...");
    doorLock.write(90);  // 90 degrees = unlocked position
    digitalWrite(buzzerPin, HIGH);
    delay(500);  // Short beep
    digitalWrite(buzzerPin, LOW);

    // Update Firebase door state to true (unlocked)
    String statePath = "/users/" + uid + "/door/state";
    if (Firebase.setBool(fbdo, statePath, true)) {
      Serial.println("Door state updated to true (unlocked).");
    } else {
      Serial.print("Failed to update door state: ");
      Serial.println(fbdo.errorReason());
    }

    // Clear password
    Firebase.setString(fbdo, "/users/" + uid + "/door/clientpass", "null");
  } else {
    Serial.println("Incorrect password. Access denied.");
    digitalWrite(buzzerPin, HIGH);
    delay(1000);  // Longer beep for incorrect password
    digitalWrite(buzzerPin, LOW);
  }
}

void processStreamData(FirebaseData &data) {
  if (data.streamTimeout()) {
    Serial.println("Stream timeout, resuming...");
    if (!Firebase.beginStream(fbdo, "/users/" + uid)) {
      Serial.println("Could not resume stream");
    }
    return;
  }

  if (data.streamAvailable()) {
    String path = data.dataPath();
    String eventType = data.eventType();
    String dataType = data.dataType();
    
    Serial.println("Change detected!");
    Serial.println("Path: " + path);
    Serial.println("Event Type: " + eventType);
    Serial.println("Data Type: " + dataType);
    
    // Debug data based on type
    if (dataType == "boolean") {
      Serial.println("Boolean Value: " + String(data.boolData()));
    } else if (dataType == "string") {
      Serial.println("String Value: " + data.stringData());
    } else if (dataType == "int") {
      Serial.println("Int Value: " + String(data.intData()));
    } else if (dataType == "json") {
      Serial.println("JSON: " + data.jsonString());
    }

    // Handle light control
    if (path.indexOf("/lights/") >= 0 && path.endsWith("/state")) {
      // Extract the pin number from the path
      int startPos = path.indexOf("/lights/") + 8;
      int endPos = path.indexOf("/state");
      if (startPos >= 8 && endPos > startPos) {
        String pinStr = path.substring(startPos, endPos);
        int pin = pinStr.toInt();
        
        // Verify this is a valid light pin
        bool validPin = false;
        for (int i = 0; i < sizeof(lightPins) / sizeof(lightPins[0]); i++) {
          if (lightPins[i] == pin) {
            validPin = true;
            break;
          }
        }
        
        if (validPin && dataType == "boolean") {
          bool state = data.boolData();
          digitalWrite(pin, state ? HIGH : LOW);
          Serial.println("Light pin " + String(pin) + (state ? " ON" : " OFF"));
        }
      }
    }
    
    // Handle door control - from client password
    else if (path == "/door/clientpass" && dataType == "string") {
      String clientpass = data.stringData();
      if (clientpass.length() > 0 && clientpass != "null") {
        Serial.println("Client Password received: " + clientpass);
        validateAndUnlock(clientpass);
      }
    }
    
    // Handle door state changes directly
    else if (path == "/door/state" && dataType == "boolean") {
      bool doorState = data.boolData();
      Serial.print("Door state changed to: ");
      Serial.println(doorState ? "true (unlocked)" : "false (locked)");

      if (doorState) {
        doorLock.write(90);  // Unlocked position
        Serial.println("Door unlocked via Firebase state change");
      } else {
        doorLock.write(0);  // Locked position
        Serial.println("Door locked via Firebase state change");
        
        // Clear the client password after locking the door
        String passPath = "/users/" + uid + "/door/clientpass";
        if (Firebase.setString(fbdo, passPath, "null")) {
          Serial.println("Client password cleared.");
        }
      }
    }
    
    // Handle resetting any sensor alerts
    else if (path.indexOf("/sensors/") >= 0 && path.endsWith("_alert") && dataType == "boolean") {
      bool alertState = data.boolData();
      Serial.println("Sensor alert state changed: " + path + " = " + String(alertState ? "true" : "false"));
      // We don't need to do anything when it's reset to false from the app
    }
    
    // Handle door object with both properties in JSON format
    else if (path == "/door" && dataType == "json") {
      Serial.println("Processing door JSON data");
      FirebaseJson json;
      json.setJsonData(data.jsonString());
      
      // Extract client password
      FirebaseJsonData jsonData;
      json.get(jsonData, "clientpass");
      if (jsonData.success && jsonData.type == "string") {
        String clientpass = jsonData.stringValue;
        if (clientpass.length() > 0 && clientpass != "null") {
          Serial.println("Client Password from JSON: " + clientpass);
          validateAndUnlock(clientpass);
        }
      }
      
      // Extract door state
      json.get(jsonData, "state");
      if (jsonData.success && jsonData.type == "boolean") {
        bool doorState = jsonData.boolValue;
        Serial.print("Door state from JSON: ");
        Serial.println(doorState ? "true (unlocked)" : "false (locked)");
        
        if (!doorState) {  // If state is false (locked)
          doorLock.write(0);  // Lock the door
          Serial.println("Door locked via JSON state change");
        } else {
          doorLock.write(90);  // Unlock the door
          Serial.println("Door unlocked via JSON state change");
        }
      }
    }
  }
}