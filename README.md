# ParkingApp
***
# Explaination of Organization
#### Mobile application: 
* Responsible for UI and lets users either reserve and "pay" for parking spots, or register their own parking spots to sell. Stores all user information in Firebase.

#### Version-control Procedures
* The main branch is for finalized code only. Push your testing code to a separate branch.
* The code will be modular, so each branch will be thoroughly tested before being added to the main project.
* The dev branch contains code to be tested before being pushed to the main branch. This will prevent breaking code in main.
* Notify the group via Discord once you have pushed new, working code to the main branch.

#### Tool Stack Description
* **Java**: Coding language that we will use for the backend
* **Github**: Version control tool that we will use to collaborate on the project across multiple machines
* **Android Studio**: IDE for Android app development and provides Android emulator for development during testing.
* **Google Docs**: Online documentation tool that we will use to plan for the project
* **Discord**: Online chat server that we will use to communicate
* **Draw.io**: Online diagramming tool that we will use to create Use Case Diagrams and UML Class Diagrams
* **Trello**: Online task management tool that we will use to track goals and progress

#### Unit Testing Instructions
* Since JUnit4 would not exactly work to catch errors effectively, the team took a more hands on approach to testing the integrity of the code.
* Added messages that would display information being passed to app. 
* Added code to terminate program and return an error message if invalid behavior occurred.
* Sent messages to UI to display error messages to user.
* Tried to cause the app to crash by entering invalid data and cause unintended app activity when running application

#### System Testing Instructions
* We will spend time interacting with the app between changes to ensure that it functions normally.
* We will test the security features of the app to ensure that it is safe for customers to use.

---
# Build Instructions
### 1. Download the code.
* If you download the `.zip` file, extract the zip file to a location you can easily find.
* Otherwise, download the code via HTTPS or SSH to a location you can easily find.
* **Important**: Don't rename the `.zip` or extracted file, as this will cause Android Studio to have a hard time running the app on the emulator.
### 2. Download and Install Android Studio.
[Download Android Studio  ](https://developer.android.com/studio/)
##### During Installation 
Make sure you check the **Android Virtual Device** checkbox during installation.
### 3. Open the `ParkingApp` folder in Android Studio.
* Launch Android Studio and select **Open an Existing Project**.
* Find the `ParkingApp` folder you extracted from the `.zip` or downloaded.
* Click **Ok** to open the folder in Android Studio.
### 4. Install the Android Emulator.
##### Open AVD Manager:
* In the toolbar at the top of the screen, find **Tools** > **AVD Manager**.
##### Add New Android Emulator:
* Select the **Create Virtual Device** button.
* Make sure that **Phone** is selected in the Category tab on the left and side.
* Scroll down to find `Pixel 3a` and click the **Next** button.
* Find the `R` system image and click **download** to download the image, then click the **Next** button.
* You can rename the emulator name if you want then click the **Finish** button.
### 5. Run the Code.
* Click the AVD Manager dropdown at the top of the screen and select the **Pixel 3a** device you just installed.
* Lastly, click the green triangle or press `Shift+F10` to run the code.
### 6. User Authorization
* New users can create a new account of owner, customer, or attendant.  
* Click the button of the type of account you would like to make, then enter in an email under user and a password that's at least 8 characters long.
* Click sign-up and you will enter the account view of the user type you created.
##### Log-in Credentials
* All passwords are `password`
###### Usernames:
* attendant@gmail.com
* attendant2@gmail.com
* attendant3@gmail.com
* customer@gmail.com
* customer2@gmail.com
* customer3@gmail.com
* owner@gmail.com
* owner2@gmail.com
* owner3@gmail.com
