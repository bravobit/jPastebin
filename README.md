jPastebin
=========

### About

jPastebin is a complete pastebin.com API wrapper for Java. It's an implementation of the full [pastebin API](http://pastebin.com/api/ "Pastebin API reference").

You can generate a paste with just a few lines!

### Developer key

In order to use this API you´ll need a [pastebin developer key](http://pastebin.com/api#1).

### Features

This API can do much more than just generating pastes.

* Creating pastes
* Getting trending pastes
* Getting pastes created by a user
* Deleting a paste
* Getting a users information and setting
* Getting raw paste contents

### Documentation

This API wrapper is fully documentated!
You can find the javadoc [here](http://bravobit.github.io/jPastebin/).

### Examples

Having trouble starting? Check out some examples to get you started.

##### Simple example
```java
String developerKey = "INSERT DEVELOPER KEY HERE";
String title = "My first jPastebin paste!"; // insert your own title
String contents = "Hello world"; // insert your own paste contents
		
// paste, get URL & print
System.out.println(Pastebin.pastePaste(developerKey, contents, title));
```
##### More examples

You can find more advanced examples in the [examples package](https://github.com/BrianBB/jPastebin/tree/master/examples)

### Issues

Having issues? Post a [new issue](https://github.com/BrianBB/jPastebin/issues/new).

---

##### Version

The current version of this API is v1.0. This API uses the latest official pastebin.com API.


