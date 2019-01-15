# Difusion Communication

# Usage
java Constructor TYPE algorihm_id graph_size delay timeout

(gossip doesn't use timeout)

## Fully functional graphs
### 1 - Barabasi Albert
### 3 - Chvatal
### 4 - Dorogovtsev-Mendes
### 6 - Full Graph
### 9 - Lobster
### 10- Petersen
### 11- RandomEuclidean

## Non functional graphs
### Chain
Leading to NullPointerException at connection_socket.receive(receive_packet);
### Flower Snark
Weird node identification
### Grid
Weird node identification
### Incomplete Grid
Weird node identification


## Installing Java

### Download and install Java SE Development Kit

https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html

### Add system variables

In the system variables, alter the variable "Path" and add "C:\Program Files\Java\jdk1.8.0_191\bin"
**check if your instalation path is equal**

## Running java programs

### Compiling

Use "javac +file+.java"
Eg. javac helloworld.java

### Running

Use "java +classname+"
Eg. java HelloWorld

## Java sockets exaple
### In separate terminals

    java Server
    java Client

## Java Graphs visualization

http://graphstream-project.org/
