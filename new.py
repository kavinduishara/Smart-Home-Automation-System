include <ESP8266WiFi.h>
include <ESP8266WiFiMulti.h>
include <ESP8266HTTPClient.h>
include <WiFiClientSecureBearSSL.h>

ifndef STASSID
define STASSID "Airtel_Zerotouch"
define STAPSK "1234567890"
endif

define switch1 16
define switch2 5
define switch3 4
define switch4 0

ESP8266WiFiMulti WiFiMulti;
const char* host = "https://home-automation-assistant.onrender.com/cmd_out";
const char* apiKey = "YOUR_SECURE_API_KEY";  // Secure API Key for authentication

void setup() {
  Serial.begin(115200);
  
  pinMode(switch1, OUTPUT);
  pinMode(switch2, OUTPUT);
  pinMode(switch3, OUTPUT);
  pinMode(switch4, OUTPUT);

  digitalWrite(switch1, HIGH);  // Active-Low Relay  
  digitalWrite(switch2, HIGH);
  digitalWrite(switch3, HIGH);
  digitalWrite(switch4, HIGH);
  
  WiFi.mode(WIFI_STA);
  WiFiMulti.addAP(STASSID, STAPSK);
  Serial.println("Connecting to ssid '" STASSID "'");
}

void secureRequest() {
  if (WiFiMulti.run() == WL_CONNECTED) {
    std::unique_ptr<BearSSL::WiFiClientSecure> client(new BearSSL::WiFiClientSecure);
    
    // Use a certificate fingerprint for verification (replace with your server’s fingerprint)
    client->setFingerprint("XX XX XX XX XX XX XX XX XX XX XX XX XX XX XX XX XX XX XX XX");

    HTTPClient https;
    if (https.begin(*client, host)) {
      https.addHeader("Authorization", apiKey);  // Add API key for authentication
      
      int httpCode = https.GET();
      if (httpCode > 0) {
        if (httpCode == HTTP_CODE_OK || httpCode == HTTP_CODE_MOVED_PERMANENTLY) {
          String command = https.getString();
          Serial.println(command);
          handleCommand(command);
        }
      } else {
        Serial.printf("[HTTPS] GET failed, error: %s\n", https.errorToString(httpCode).c_str());
      }
      https.end();
    } else {
      Serial.println("[HTTPS] Unable to connect securely.");
    }
  }
}

void handleCommand(String command) {
  if (command == "turn on switch 1") {
    digitalWrite(switch1, LOW);
  } else if (command == "turn off switch 1") {
    digitalWrite(switch1, HIGH);
  } else if (command == "turn on switch 2") {
    digitalWrite(switch2, LOW);
  } else if (command == "turn off switch 2") {
    digitalWrite(switch2, HIGH);
  } else if (command == "turn on switch 3") {
    digitalWrite(switch3, LOW);
  } else if (command == "turn off switch 3") {
    digitalWrite(switch3, HIGH);
  } else if (command == "turn on switch 4") {
    digitalWrite(switch4, LOW);
  } else if (command == "turn off switch 4") {
    digitalWrite(switch4, HIGH);
  } else if (command == "turn on all switches") {
    digitalWrite(switch1, LOW);
    digitalWrite(switch2, LOW);
    digitalWrite(switch3, LOW);
    digitalWrite(switch4, LOW);
  } else if (command == "turn off all switches") {
    digitalWrite(switch1, HIGH);
    digitalWrite(switch2, HIGH);
    digitalWrite(switch3, HIGH);
    digitalWrite(switch4, HIGH);
  }
}

void loop() {
  secureRequest();
  delay(500);  // Prevent excessive requests
}
