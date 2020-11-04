
**COMPILE:**

./compile.bat

**RUN:**

./run.bat

**NOTES:**

no need "previous stop"

FlightInfo need a linkedlist to store how many flights need to go to and which is the current flight

Everytime visit node, find next location going to (linked list attribute needed in FlightInfo class)

When initially load data with 2 pit stops, need to split into 3 flight info objects
so AB, BC, and CD.

For BC and CD, we need to store a boolean that it is part of a pitstop 
flight (eg. isOrigin) so cannot start from here (or can just skip here)