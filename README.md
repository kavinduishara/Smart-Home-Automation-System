# Smart Home Integration System

## Project Overview
The Smart Home Integration System is a comprehensive home automation solution that combines multiple subsystems to enhance home safety, efficiency, and convenience through embedded systems technology.

### Key Features
- **Smart door lock** with keypad and remote access
- **Automatic water level monitoring** and pump control
- **Voice-controlled switch management** system
- **Environmental sensing** and monitoring

## System Architecture
The system uses a distributed architecture with:
- Individual control modules for each subsystem
- Firebase as the central database and communication hub
- Web/mobile interfaces for remote monitoring and control
- Voice recognition for hands-free operation
- Sensor networks for environmental awareness

## Hardware Requirements

### General
- ESP32 and ESP8266 microcontrollers
- Hi-Link HLK-5M05 power supply
- Various connecting wires and breadboards
- USB cables for programming

### Smart Door Lock
- ESP32 microcontroller
- Servo motor for lock mechanism
- 4×4 Matrix keypad
- Jumper wires

### Water Pump Control
- ESP8266 microcontroller
- Ultrasonic sensor (HC-SR04)
- OLED display
- Relay module
- Water pump (for demonstration)

### Voice-Controlled Switch
- ESP32 microcontroller
- 74HC595 shift register ICs
- LEDs (representing home appliances)
- Resistors (220Ω)
- Microphone module

### Environmental Monitoring
- ESP32 microcontroller
- Electret Condenser 9mm Microphone Sound Sensor
- LEDs for status indication

## Software Requirements
- Arduino IDE
- ESP32/ESP8266 board support packages
- Required libraries:
  - Firebase ESP Client
  - WiFi
  - Servo
  - Wire
  - Adafruit SSD1306 (for OLED)
  - ArduinoJson
  - ESP32 BLE
- Web application (HTML, CSS, JavaScript)
- Firebase account for backend services

## Installation

### Setting up Arduino IDE
1. Download and install Arduino IDE from [arduino.cc](https://www.arduino.cc/en/software)
2. Add ESP32/ESP8266 board support:
   - Go to File > Preferences
   - Add the following URLs to Additional Board Manager URLs:
     - https://dl.espressif.com/dl/package_esp32_index.json
     - http://arduino.esp8266.com/stable/package_esp8266com_index.json
   - Go to Tools > Board > Boards Manager
   - Search for and install ESP32 and ESP8266 packages

### Installing Required Libraries
In Arduino IDE, go to Sketch > Include Library > Manage Libraries, then search for and install:
- Firebase ESP Client
- Servo
- Adafruit SSD1306
- ArduinoJson

### Firebase Setup
1. Create a Firebase account at [firebase.google.com](https://firebase.google.com/)
2. Create a new project
3. Set up Realtime Database
4. Note your Firebase credentials (API key, database URL)
5. Set up authentication method (Email/Password recommended)

### Web Application Setup
1. Clone the web application repository
2. Update Firebase configuration in `config.js` with your credentials
3. Deploy to hosting service or run locally

## Configuration

### Smart Door Lock Configuration
1. Open the `smart_door_lock.ino` file
2. Update WiFi credentials:
   ```cpp
   #define WIFI_SSID "your_wifi_ssid"
   #define WIFI_PASSWORD "your_wifi_password"
   ```
3. Update Firebase credentials:
   ```cpp
   #define API_KEY "your_firebase_api_key"
   #define DATABASE_URL "your_firebase_database_url"
   ```
4. Set default PIN code:
   ```cpp
   const char* DEFAULT_PIN = "1234";
   ```
5. Configure servo pin and positions:
   ```cpp
   #define SERVO_PIN 13
   #define LOCKED_POSITION 0
   #define UNLOCKED_POSITION 90
   ```

### Water Pump Control Configuration
1. Open the `water_pump_control.ino` file
2. Update WiFi and Firebase credentials
3. Configure pins:
   ```cpp
   #define TRIGGER_PIN 12
   #define ECHO_PIN 14
   #define RELAY_PIN 5
   ```
4. Set water level thresholds:
   ```cpp
   #define LOW_LEVEL_THRESHOLD 10  // 10%
   #define HIGH_LEVEL_THRESHOLD 90 // 90%
   ```

### Voice-Controlled Switch Configuration
1. Open the `voice_controlled_switch.ino` file
2. Update WiFi and Firebase credentials
3. Configure shift register pins:
   ```cpp
   #define DATA_PIN 14   // DS pin
   #define CLOCK_PIN 12  // SH_CP pin
   #define LATCH_PIN 13  // ST_CP pin
   ```

### Environmental Monitoring Configuration
1. Open the `environmental_monitoring.ino` file
2. Update WiFi and Firebase credentials
3. Configure sound sensor pin:
   ```cpp
   #define SOUND_SENSOR_PIN 34
   ```
4. Set sound threshold:
   ```cpp
   #define SOUND_THRESHOLD 2000
   ```

## Usage

### Smart Door Lock
- Enter the correct PIN on the keypad to unlock the door
- Use the web/mobile application to remotely lock/unlock
- View access logs through the application

### Water Pump Control
- The system automatically monitors water levels
- Pump activates when water level drops below threshold
- Use the web/mobile application to monitor levels and manually control the pump

### Voice-Controlled Switch
- Use voice commands to control appliances:
  - "Light on" / "Light off"
  - "Fan on" / "Fan off"
  - etc.
- Use the web/mobile application for remote control

### Environmental Monitoring
- The system continuously monitors sound levels
- Receive notifications for unusual sounds
- View historical data through the web/mobile application

## Troubleshooting

### Connection Issues
- Verify WiFi credentials
- Check Firebase credentials and security rules
- Ensure microcontrollers are powered properly
- Verify all connections according to circuit diagrams

### Smart Door Lock Issues
- Check servo connections
- Verify keypad wiring
- Reset the system and reconfigure PIN

### Water Pump Control Issues
- Check ultrasonic sensor positioning
- Verify relay connections
- Calibrate water level readings

### Voice-Controlled Switch Issues
- Check shift register connections
- Verify LED connections
- Test each output individually

### Environmental Monitoring Issues
- Check sound sensor positioning
- Calibrate sound threshold based on environment
- Verify notification settings

## Future Enhancements
- AI-based anomaly detection for home security
- Energy consumption monitoring and optimization
- Additional sensor integration (temperature, humidity, gas)
- Advanced voice recognition with natural language processing
- Machine learning for predictive maintenance and user patterns




