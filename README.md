# FireWay
 Raspberry pI3 AndroidThings Firebase RealTime Database and WebApplication client
Simulation project for the control and verification of the state of a remote road fire

## Introducing "FireWay"... ##

<img src="art/power_on.png" alt="phone image" width="200px" />
<img src="art/power_off.png" alt="phone image" width="200px" />

I decided to use a Raspberry Pi 3 running Android Things and Firebase Realtime Database to control and verificatiion state of road fire. 

Mainly because Firebase has a *VERY* powerful tool for monitoring if a client is connected to your Realtime database or not. 

## Setup Requirements

In order to get the app running yourself, you need to:

1. Checkout this repository.
2. Create a new Firebase Project [here](https://firebase.google.com).
3. Download the *google-service.json* file from the Firebase Console to both the app folder and the companion-app folder.
4. Set the Realtime database rules to be read and write for everyone (Firebase Console -> Database -> Rules). (WARNING: This means there is NO security on our database – you should not have these rules in production)

	    {
	     "rules": {
	         ".read": true,
	         ".write": true
	       }
	    }

5. Deploy the “Things” module to the Raspberry Pi or equivalent Android Things device (you need to make sure you have setup your Pi with the Android Things OS).
6. Deploy the “companion-app” module to your phone.





## References

- Building an Online Presence System using Firebase Realtime Database - https://firebase.googleblog.com/2013/06/how-to-build-presence-system.html
- Android Things Setup - https://developer.android.com/things/index.html
