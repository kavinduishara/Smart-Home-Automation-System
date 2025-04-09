
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

#define WIFI_SSID "YOUR_WIFI_SSID"
#define WIFI_PASS "YOUR_WIFI_PASSWORD"


#define FIREBASE_URL "https://your-project-id.firebaseio.com/door_status.json"


#define ROW1 32
#define ROW2 33
#define ROW3 25
#define ROW4 26
#define COL1 27
#define COL2 14
#define COL3 12
#define COL4 13


#define BUZZER_PIN 2
#define SERVO_PIN 15

static const char *TAG = "SMART_LOCK";

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


void servo_init() {
    ledc_timer_config_t timer = {
        .speed_mode = LEDC_LOW_SPEED_MODE,
        .timer_num = LEDC_TIMER_0,
        .duty_resolution = LEDC_TIMER_16_BIT,
        .freq_hz = 50,
        .clk_cfg = LEDC_AUTO_CLK
    };
    ledc_timer_config(&timer);

    ledc_channel_config_t channel = {
        .channel = LEDC_CHANNEL_0,
        .duty = 0,
        .gpio_num = SERVO_PIN,
        .speed_mode = LEDC_LOW_SPEED_MODE,
        .timer_sel = LEDC_TIMER_0
    };
    ledc_channel_config(&channel);
}

void set_servo_angle(int angle) {
    uint32_t duty = (angle * (6553 - 3277) / 180) + 3277; 
    ledc_set_duty(LEDC_LOW_SPEED_MODE, LEDC_CHANNEL_0, duty);
    ledc_update_duty(LEDC_LOW_SPEED_MODE, LEDC_CHANNEL_0);
}


void buzzer_beep() {
    gpio_set_level(BUZZER_PIN, 1);
    vTaskDelay(pdMS_TO_TICKS(200));
    gpio_set_level(BUZZER_PIN, 0);
}

const char KEYS[4][4] = {
    {'1','2','3','A'},
    {'4','5','6','B'},
    {'7','8','9','C'},
    {'*','0','#','D'}
};

int row_pins[4] = {ROW1, ROW2, ROW3, ROW4};
int col_pins[4] = {COL1, COL2, COL3, COL4};

void keypad_init() {
    for (int i = 0; i < 4; i++) {
        gpio_set_direction(row_pins[i], GPIO_MODE_OUTPUT);
        gpio_set_level(row_pins[i], 1);
    }
    for (int i = 0; i < 4; i++) {
        gpio_set_direction(col_pins[i], GPIO_MODE_INPUT);
        gpio_set_pull_mode(col_pins[i], GPIO_PULLUP_ONLY);
    }
}

char keypad_get_key() {
    for (int row = 0; row < 4; row++) {
        gpio_set_level(row_pins[row], 0);
        for (int col = 0; col < 4; col++) {
            if (gpio_get_level(col_pins[col]) == 0) {
                vTaskDelay(pdMS_TO_TICKS(50)); 
                if (gpio_get_level(col_pins[col]) == 0) {
                    gpio_set_level(row_pins[row], 1);
                    return KEYS[row][col];
                }
            }
        }
        gpio_set_level(row_pins[row], 1);
    }
    return '\0';
}


void send_status_to_firebase(const char* status) {
    esp_http_client_config_t config = {
        .url = FIREBASE_URL,
        .method = HTTP_METHOD_PUT,
    };
    esp_http_client_handle_t client = esp_http_client_init(&config);
    char json_body[64];
    sprintf(json_body, "\"%s\"", status);
    esp_http_client_set_header(client, "Content-Type", "application/json");
    esp_http_client_set_post_field(client, json_body, strlen(json_body));
    esp_err_t err = esp_http_client_perform(client);
    if (err == ESP_OK) {
        ESP_LOGI(TAG, "Firebase Updated: %s", status);
    } else {
        ESP_LOGE(TAG, "HTTP Error: %s", esp_err_to_name(err));
    }
    esp_http_client_cleanup(client);
}

void app_main(void) {
    // Init
    nvs_flash_init();
    wifi_init_sta();

    gpio_set_direction(BUZZER_PIN, GPIO_MODE_OUTPUT);
    keypad_init();
    servo_init();

    char code[5] = "";
    int index = 0;

    while (1) {
        char key = keypad_get_key();
        if (key != '\0') {
            printf("Key Pressed: %c\n", key);
            buzzer_beep();

            if (key == '#') {
                code[index] = '\0';
                if (strcmp(code, "1234") == 0) {
                    ESP_LOGI(TAG, "Access Granted");
                    set_servo_angle(90);
                    send_status_to_firebase("unlocked");
                    vTaskDelay(pdMS_TO_TICKS(5000));
                    set_servo_angle(0);
                    send_status_to_firebase("locked");
                } else {
                    ESP_LOGI(TAG, "Access Denied");
                    send_status_to_firebase("failed_attempt");
                }
                index = 0;
                memset(code, 0, sizeof(code));
            } else if (key == '*') {
                index = 0;
                memset(code, 0, sizeof(code));
            } else if (index < 4) {
                code[index++] = key;
            }
        }
        vTaskDelay(pdMS_TO_TICKS(50));
    }
}
