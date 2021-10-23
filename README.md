# com.hnievat.controlluces

An Android application to control an Arduino-based Smart Plug/Smart Outlet uisng an HC-05 Bluetooth module and a built-in photo-resistor to sense ambient light, complete with auto-on/off set-up and energy monitoring features, made for a college course.

Built using Android Studio Arctic Fox 2020.3.1, with Target SDK version 29.

Based on [Android-App-HC05-Arduino](https://github.com/Priyanka4pc/Android-App-HC05-Arduino) by @Priyanka4pc.

## Features

- Firebase-based login screen (as required by the course, can be easily disabled if wanted)
- Bluetooth device search screen
- Saves last used device
- Home screen with ambient light sensor info and manual on/off button
- Settings screen for automatic on/off setup using ambient light sensor thresholds
- Usage stats screen for energy monitoring, featuring activity log, power-on hours, and energy usage calculation using user-configurable consumption and electricity rates
- Basic sanity-check functions for received data
- About screen

## Issues/Known bugs/Future plans

- Login screen not very useful
- Device search screen will connect to any device, not just an HC-05
- No automatic refresh, data must be manually requested by the user
- Received data is often corrupt, apparently a bug from the Android BT API side
- Automatic on/offs aren't logged in the activity log
- ALS levels are shown in raw decimal values, could be shown in Lux if the appropriate conversion was made (photoresistor model-dependant)
- Strings all over the place, code needs clean-up

## Thanks

- [Android-App-HC05-Arduino](https://github.com/Priyanka4pc/Android-App-HC05-Arduino)