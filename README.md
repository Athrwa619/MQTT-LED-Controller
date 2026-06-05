# MQTT Virtual LED Controller

An Android application built in Java that demonstrates real-time, bi-directional IoT communication using the MQTT protocol. The app allows multiple Android devices to connect to a shared cloud broker and synchronize the state of a virtual LED.

## 🚀 Overview
This project was built to test and implement secure, low-latency device-to-device messaging. Instead of communicating directly with each other, the mobile devices act as IoT nodes that publish and subscribe to a central message broker. 

When a user taps the toggle button, the app publishes a command to the cloud. Any other device subscribed to the same topic instantly receives the payload and updates its interface to match, creating a synchronized state across the network.

## ✨ Key Features
* **Real-Time Synchronization:** Achieves instant state updates across multiple devices using the Publish/Subscribe architecture.
* **Military-Grade Encryption:** Wraps the MQTT traffic in standard SSL/TLS encryption to bypass aggressive mobile carrier firewalls and ensure secure data transmission.
* **Unique Device Identification:** Automatically generates a unique `UUID` for every device upon launch to prevent infinite echo loops (devices ignore their own published messages).
* **Enterprise Cloud Broker:** Connects reliably to the public EMQX enterprise broker.

## 🛠️ Tech Stack
* **Language:** Java
* **Environment:** Android Studio
* **Networking:** HiveMQ MQTT Client Library
* **Protocol:** MQTT v3.1.1 (Secure Port 8883)
* **Broker:** `broker.emqx.io`

## 🧠 How the Logic Works
1. **Connect:** Upon launch, the app establishes an asynchronous, encrypted connection to the EMQX broker.
2. **Subscribe:** A background callback listener subscribes to the `optimuslogic/led_control_project` topic.
3. **Publish:** Clicking the "Toggle" button sends a payload containing the device's `UUID` and the word `TOGGLE`.
4. **Execute:** If the background listener hears the word `TOGGLE` from a *different* UUID, it safely pushes an update to the Main UI Thread to change the LED View color (Red/Green).

## 📥 Installation
1. Clone this repository to your local machine.
2. Open the project in Android Studio.
3. Allow Gradle to sync and download the HiveMQ dependencies.
4. Build the APK and install it on a physical Android device (Requires active Wi-Fi or Cellular Data). 
*(Note: Because of the SSL handshake and hardcoded certificate checks, testing on physical hardware is recommended over the Android Emulator).*
