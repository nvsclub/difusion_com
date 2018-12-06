# Difusion Communication

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

# Anti entropy Vs Gossip : Objetivos do projeto

Temos de criar nos e utilizá-los em topologias diferentes.

Vamos definir várias topologias para depois compará-las.

Os nos vão ter 2 sockets, e em uma abordagem inicial 2. 
threads (uma servidor outra cliente).

O nosso objetivo é trocar 3 bytes (RGB).

Usar um programa que nos dá a topologia da rede e pintar os nos com estas RGB. Utilizar estes valores para pintar as bolas dos nos. Pintar as linhas para ver por onde passou.

Ver como criar /utilizar os algoritmos.

Analisar as diferentes performances (para os 2 algoritmos e para redes diferentes):
- Percentagem de nós até onde chegou a mensagem,
- Percentagem de nós que receberam a mensagem correta dos que receberam;
- Percentagem de nós que receberam a mensagem correta;
- Análise temporal;

## Implementation

### Class Main
- Creates graph topology
- Creates the nodes in the topology and mantains a connection to them in order to verify state
- Invoques visualization module to check the state of the network

### Class Node
- Creates a thread for server and a thread for clients OR
- Creates a server thread and when comunications are needed creates a thread for the client OR
- Creates a server thread and does the client comunication in the same thread