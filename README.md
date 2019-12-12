# Digital Identity
### Capstone Client - Tata Consultancy Services

The project has several components - 
- Producer Server
- Consumer server
- Android Application
- Blockchain

To run the Android app -  
1. Navigate to https://firebase.google.com/docs/android/setup#console
2. Follow the steps prescribed under the section "Option1: Add Firebase using the Firebase console"
3. In step 2, use the Android package name as "com.example.producerapp." These steps are required to acquire the "google-services.json" file for your device. 
4. Navigate to your Firebase console and click on your project. Click on the settings icon, select "Project settings" and navigate to the "Cloud Messaging" tab.
5. Make a note of the values in the fields "Sender ID" and "Server key".
6. Run the app on your device. Go to your Firebase project, go to Cloud Messaging and create a new notification with the following fields:  
    Notification
      Notification title = Test Notification
      Notification text = Test Notification Text
    Target
      App = com.example.producerapp
    Scheduling
      Send to eligible users = Now
    Additional options
      Custom data (Add 1 key-value pair here)
        message = Authentication
		
7. You will receive a notification on your app. The Android Studio console will display a message that says "YOUR TOKEN IS: <token value> ." Make a note of this token value. 
8. The app may crash if you click on the notification and select yes/no, since it has not been configured completely.
 

To run the servers - 
<--------MAKE CHANGES HERE ---------------------->
1. Go to **src->main->java->edu.cmu.consumerserver-> .......** and paste the value of "Sender ID" from the previous step in the "projectId" field and the "Server key" in the "apiKey" field.  
2. Paste the token value in the field "Device_ID." 


3. Download and Install Ganache GUI from - https://www.trufflesuite.com/ganache
4. Open Ganache, Settings, and make sure that the Server Port number is 8545
5. On the Accounts page, you will have 10 accounts and their corresponding addresses.
6. Click on the "key" icon of one of the accounts and copy the Private key value to PRIVATE_KEY variable in the Transaction class of the **blockchain** folder, **src->main->java->edu.cmu.producerserver->service Transaction** class, and **src->main->java->edu.cmu.consumerserver->service Transaction** class.
7. Run the **Blockchain** class in the **blockchain** folder.
8. There will be an address printed on the console. This is the address where the Contract is deployed. This contract holds a map of the ID and the public keys for the producer and consumer that we have defined for the purpose of demonstration of this prototype.
9. Copy the address and paste it to the **deployedAddress** variable in **src->main->java->edu.cmu.producerserver->service Transaction** and **src->main->java->edu.cmu.consumerserver->service Transaction** class.
10. Run the Producer server and the Consumer server 
11. Go to the command line and make a note of the producer server's IP address
12. In the android project, go to **app->src->main->java->com->example->producerapp->ServerDetails**. Set urlString to "http://<producer server's IP address>:8082". Run the app again.
13. Run the different stages of the application.



