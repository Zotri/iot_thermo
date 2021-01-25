# iot_thermo Android Application 

this app is a part of a scholar project at the FH-Campus Wien and deals with internet of things applications.
This repository contains the practical part of the project. 


## Idea

The main part of the app is to establish a connection with MQTT broker to reads out what published their. A main publisher roll goes to
a simple temperature sensor fixed with a D1 mini microcontroller with tasmota software flashed on it. The app should represent the transfered data over 
your home network to a friendly UI where it is readable, where the app is a MQTT client/sbuscriber. When the user changes the temp value the app should 
register itself as a MQTT client/publisher.



## Project technical tools related to the APP

- MQTT mosquitto. Home installation
- D1 mini microcontroller + temp sensor + relay switch
- HiveMQ java library + http/https connecter interface 

## Restrictions

- The application is set at minimum comlexity to connect and disconnect based on a static topic url. 
- Few Error handlers.
- Conversion from string to float or parsing values is done with error handling and will stop the application if it happens.
- NumberPicker is a programmable java class imported to this project not done by me!

## Future Release

- Handling User insertions.
- Make resiellient xml-layout (constraint or relative not combination between more than that)




