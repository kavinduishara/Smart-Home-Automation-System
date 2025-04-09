// Include necessary libraries
#include <stdio.h>
#include <string.h>
#include "freertos/FreeRTOS.h"
#include "freertos/task.h"
#include "esp_wifi.h"
#include "esp_event.h"
#include "esp_log.h"
#include "nvs_flash.h"
#include "esp_http_client.h"
#include "driver/gpio.h"
#include "driver/ledc.h"
#include "esp_timer.h"

// WiFi credentials
#define WIFI_SSID "gl76"
#define WIFI_PASS "11111111"

// Firebase URL
#define FIREBASE_URL "https://home-automation-de3c9-default-rtdb.firebaseio.com/"

// Keypad GPIOs
#define ROW1 32
#define ROW2 33
#define ROW3 25
#define ROW4 26
#define COL1 27
#define COL2 14
#define COL3 12
#define COL4 13

// Buzzer and Servo GPIO
#define BUZZER_PIN 2
#define SERVO_PIN 15

static const char *TAG = "SMART_LOCK";

// ================= WIFI SETUP =================
void wifi_init_sta() {
    ESP_ERROR_CHECK(esp_netif_init());
    ESP_ERROR_CHECK(esp_event_loop_create_default());
    esp_netif_create_default_wifi_sta();

    wifi_init_config_t wifi_config = WIFI_INIT_CONFIG_DEFAULT();
    ESP_ERROR_CHECK(esp_wifi_init(&wifi_config));

    wifi_config_t sta_config = {
        .sta = {
            .ssid = WIFI_SSID,
            .password = WIFI_PASS,
        },
    };
    ESP_ERROR_CHECK(esp_wifi_set_mode(WIFI_MODE_STA));
    ESP_ERROR_CHECK(esp_wifi_set_config(WIFI_IF_STA, &sta_config));
    ESP_ERROR_CHECK(esp_wifi_start());
    ESP_ERROR_CHECK(esp_wifi_connect());
    ESP_LOGI(TAG, "WiFi Initialized.");
}


    
