# PKI branch of SharkNETNG

In order to work with a version of SharkFW this branch contains a working revision of the framework.

* Revision forked: 59b4d53 (`git checkout -b pki 59b4d53`)
* Revision of sharkFW in use and copied within link.sh: 34b2f4e

------------

* Initially `link.sh` was executed, but now all copied resorces are added to this branch in __app/src/main/java/net/sharkfw__
* In __app/src/main/java/de/htw_berlin/sharkandroidstack/sharkFW/peer/AndroidSharkEngine.java__ import/usage of _SharkStub_ had to be replaced with _KEPStub_
* JUnit was missing and added to gradle build file

