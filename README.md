This repository contains code to reproduce results in paper "A query language for workflow logs" (ref TBA).
It includes 4 directories: 
1) main - start point of the program
2) model - implementation of log, incident tree models
3) evaluation - query evaluation engine
4) test - some tests to repro results in the paper

How to run the program?
- install java
- prepare a test file that has the same format as in the paper, fields separated by space
- put the file into a directory called "data" in the same level as the other directories
- compile the files first using `javac`, followed by all java file names (relative paths from cur dir)
- run the program using `java`, followed by the main class file name
- or you can just run the unittests from test folders to get some basic test results

The file `main.java` has some query examples and was edited to load from a fixed test data file.
In order to test new files, we need to either change the filename to "output_09.txt", or change the file name in `main.java` to the file name you have.
Also to test other queries, you need to prepare the query inside `main.java`. Details can be found in that file.
