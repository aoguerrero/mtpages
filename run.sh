#!/bin/bash
mvn clean compile -Dport=8880 -Dusername=user -Dpassword=5ebe2294ecd0e0f08eab7690d2a6ee69 -Denable_cache=false -Dwebsite=localhost exec:java

