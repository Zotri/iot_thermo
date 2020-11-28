# iot_thermo android Application 

this app is a part of a scholar project on the FH-Campus Wien and deals with internet of things applications.
This repository contains the practical part of the project. 


## Idea

The main part of the app is to establish a connection with MQTT broker to reads out what published their. A main publisher roll goes to
a simple temperature sensor fixed with a D1 mini microcontroller with tasmota software flashed on it. The app should represent the transfered data over 
your home network to a friendly UI where it is readable, where the app is a MQTT client/sbuscriber. When the user changes the temp value the app should 
register itself as a MQTT client/publisher.



## Project technical tools

- MQTT mosquitto. Home installation
- D1 mini microcontroller + temp sensor
- HiveMQ java library



