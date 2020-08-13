# EncryptionUtility
A simple console application using AES encryption to encrypt a string, and then decrypt it.

Compile with Java 8+

Run this application with the arguments "encrypt helloworld" to create two files, one file entitled "output.encrypted" which contains the salted IV and encrypted bytes of the input string, and another file entitled "output.key" which contains the private decryption key.

Run this application with the arguments "decrypt" to print out to the console the decrypted input string.
