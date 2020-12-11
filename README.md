# app_aadharkyc
app for offline aadhar verification

A simple application in which user will fill a form and 
then their details will be verified using their aadhar offline verification zip file (containing xml data).
They must type the share code to extract the password protected file.
Then we will parse the xml and get the user data in following ways
1. verifying the signature using certificate
2. verifying the user data from the form which was formed initially while registration

If we succefully verify signature and data from user then we will register the user in our application
