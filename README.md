# team-203-F18
team repo for team-203-F18

# AWS
### Link
ec2-34-221-112-30.us-west-2.compute.amazonaws.com
### Port
4545
### Usage
From the Chatter root folder (where the pom.xml is located), run the following maven command:

`mvn exec:java -Dexec.mainClass=edu.northeastern.ccs.im.chatter.CommandLineMain -Dexec.args="ec2-34-221-112-30.us-west-2.compute.amazonaws.com 4545"`

### Testing
After running the command and entering a username, send one of the reserved messages such as Hello message to get a response back from the server.

### Broadcast messaging (send to everyone)
Open up multiple terminal windows and run the maven command on each of them to create separate instances of Chatter. After that send a message from any of the instances to send it to all other connected instances.

