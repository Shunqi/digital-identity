# Digital Identity
### Capstone Client - Tata Consultancy Services

The project has several components - 
- Producer Server
- Consumer server
- Android Application
- Blockchain

To run the servers - 
1. Download and Install Ganache GUI from - https://www.trufflesuite.com/ganache
2. Open Ganache, Settings, and make sure that the Server Port number is 8545
3. On the Accounts page, you will have 10 accounts and their corresponding addresses.
4. Click on the "key" icon of one of the accounts and copy the Private key value to PRIVATE_KEY variable in the Transaction class of the **blockchain** folder, **src->main->java->edu.cmu.producerserver->service Transaction** class, and **src->main->java->edu.cmu.consumerserver->service Transaction** class.
5. Run the **Blockchain** class in the **blockchain** folder.
6. There will be an address printed on the console. This is the address where the Contract is deployed. This contract holds a map of the DID and the public keys for the producer and consumer that we have defined for the purpose of demonstration of this prototype.
7. Copy the address and paste it to the **deployedAddress** variable in **src->main->java->edu.cmu.producerserver->service Transaction** and **src->main->java->edu.cmu.consumerserver->service Transaction** class.
8. Run the Producer server and the Consumer server and run the different stages of the application.
