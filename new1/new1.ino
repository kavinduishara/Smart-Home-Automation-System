#include <Keypad.h>
#include <ESP32Servo.h>

// Servo Motor
Servo doorLock;
const int servoPin = 15;

// Buzzer
const int buzzerPin = 2;

// Keypad Configuration
const byte ROWS = 4; // Four rows
const byte COLS = 4; // Four columns
char keys[ROWS][COLS] = {
    {'1', '2', '3', 'A'},
    {'4', '5', '6', 'B'},
    {'7', '8', '9', 'C'},
    {'*', '0', '#', 'D'}
};
byte rowPins[ROWS] = {32, 33, 25, 26}; // Row pins
byte colPins[COLS] = {27, 14, 12, 13}; // Column pins
Keypad keypad = Keypad(makeKeymap(keys), rowPins, colPins, ROWS, COLS);

// Set Password
String password = "1234";
String inputPassword = "";

void setup() {
    Serial.begin(115200);
    Serial.println("System Initialized");
    doorLock.attach(servoPin, 500, 2500); // Attach servo with min/max pulse width
    pinMode(buzzerPin, OUTPUT);
    doorLock.write(0); // Lock position
    Serial.println("Door is Locked. Enter Password:");
}

void loop() {
    char key = keypad.getKey();
    if (key) {
        Serial.print("Key Pressed: ");
        Serial.println(key);
        
        if (key == '#') {
            Serial.print("Entered Password: ");
            Serial.println(inputPassword);
            
            if (inputPassword == password) {
                Serial.println("Access Granted. Unlocking Door...");
                doorLock.write(90); // Unlock door
                digitalWrite(buzzerPin, HIGH);
                delay(500);
                digitalWrite(buzzerPin, LOW);
                delay(3000);
                doorLock.write(0); // Lock door again
                Serial.println("Door Locked Again.");
            } else {
                Serial.println("Access Denied. Wrong Password!");
                digitalWrite(buzzerPin, HIGH);
                delay(1000);
                digitalWrite(buzzerPin, LOW);
            }
            inputPassword = "";
        } else if (key == '*') {
            Serial.println("Password Reset.");
            inputPassword = "";
        } else {
            inputPassword += key;
        }
    }
}


