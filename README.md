# app_aadharkyc
app for offline aadhar verification

A simple application in which user will fill a form and 
then their details will be verified using their aadhar offline verification zip file (containing xml data).
They must type the share code to extract the password protected file.
Then we will parse the xml and get the user data in following ways
1. verifying the signature using certificate
2. Checking SHA256 of email and mobile number
3. verifying the user data from the form which was formed initially while registration

If we succefully verify signature and data from user then we will register the user in our application

There are three activities:
1. GetUserData - here the user will fill the form
2. Webpage - here suer will download the xml zip file
3. Verification - verifying mobile, email, name, father name etc.

Here are the screen shots
1. GetUserData

![GetUserData](https://bn1305files.storage.live.com/y4meDVTZiMJQH17nQn00CGHHMFO4pXjfJuylcLOc0Wm2YF4A1LfXSyyrijACyCg4gaLC8fJkJgpUpguHVXOscvw-ysj0McgZFG11WTjztuQf7m5tmtPtjsKp8lMyVaXWGFs2fdMteyaGvvc3cDNRX613Vkj4v1WLTUjxMH5oa7Sd6EGO2uuG9_IyIzY5I7wLREvsjExZcnjChMWhpX0Ovytig/1%20%282%29.jpg?psid=1&width=264&height=558) -

2. Webpage (webview)

![Webpage](https://bn1305files.storage.live.com/y4mBvODQDGKRlrJllv-AclU6sDHZBc47dgpYFJaSYobxdY728ENJidQP9vgZZJMMxR9I4pIDGajihERmBloRBP6MqeKLOD_EDq6wAo7hKTCQFwS__3smnPE_cJ-PmkBTRW17pFkaLQbmwk-VVxZG8ZMIKJJpYNdUzHFQXLI8nMymcmcrcyKmDWzgC5g5f61jvq6AaPa1lmJ8_RIQrGtjTItjA/iamge.jpg?psid=1&width=260&height=548) -

3. Verification 

![Verification](https://bn1305files.storage.live.com/y4mfrVGev4KFvid6MXSsD7EnR4jxGF8M1H755LUgPddKZ3hPFF23by9U4JBXU5WAoKmAVJ6l58g5EmI2LkYLhf8210VIJCjoZ_GrSIPsFRWjfkwWNVPdy7Jb3ty1JyvAvF21b8iuPqr3RiWE-2mjWhhAtnDsiYEKA6TeTb8vLi9ts_2AN3jGigBkdbgu7X0-tht9DHLaFfbAaYr_8okUxrJJw/1%20%281%29.jpg?psid=1&width=264&height=558)
