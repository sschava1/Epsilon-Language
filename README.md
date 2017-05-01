# SER502-Spring2017-Team-13

Project : Compiler and Virtual Machine for a Programming Language

How to run Epsilon Code:

- Download EpsilonRunner.jar and EpsilonInterpreter.py and keep them in same path
- Write a code in Epsilon language and save it with an extension *.eps on the same
path where the EpsilonRunner.jar and EpsilonInterpreter.py are kept
- Open Command line and go to this path where all these 3 files are kept
- Using this command will generate the Intermediate code file with extension *.epsi on
the same path:

Java -jar EpsilonRunner.jar filename.eps

Make sure Java 8 is installed on the system with Path variables set to run above command
- To run this intermediate code generated run this command:

Python EpsilonInterpreter.py

Make sure Python 3 is installed on the system with Path variables set to run above
command
This will run your code and generate the output on command line