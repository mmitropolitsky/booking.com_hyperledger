# Blockchain Booking

## Overview

For details about the business case itself, see [Problem description](docs/problem_description/problem_description.md)

We chose to focus on the **Overbooking** sub-problem, at least for now. As such we mainly consider the following actors:
* Partner
* OTA

## Hyperledger Fabric Setup

1. [Install the pre-requisites](https://hyperledger-fabric.readthedocs.io/en/latest/prereqs.html)
2. [Install the samples, binaries and Docker images](https://hyperledger-fabric.readthedocs.io/en/latest/install.html) \
**IMPORTANT** : the docker combined docker images amount to ~16GB, the samples are ~160MB
3. [Install the necessary SDK](https://hyperledger-fabric.readthedocs.io/en/latest/getting_started.html#hyperledger-fabric-sdks) \
*or simply use the maven dependencies definitions in overbooking/pom.xml for the Java SDK*
4. [Optional] [Install Hyperledger Composer](https://hyperledger.github.io/composer/latest/installing/installing-index.html) \
*Note* : this is a development tool to facilitate the definition of **assets** ; see [Known issues](docs/knwon_issues.md) if running into issues

## Hyperledger Architecture

The logical components of Hyperledger Fabric are :
* **Assets** : tangible or intangible assets being traded/modified in the application (here *rental assets*)
* **Chaincode** : smart contracts exposing functions to operate on **assets**.
* **Blockchain** : the immutable chain where the data is stored. In Fabric it is decomposed in two components :
    * **State** : current state of the chain (a Key-Value Store)
    * **Ledger** : a historical ledger of all transactions since genesis block
* **Nodes** : the nodes involved in the different operations / management of the network
    * **Client nodes** : nodes that invoke *transactions* on **chaincode**
    * **Peer nodes** : nodes that maintain the **state** and **ledger**. Additionally they can *endorse* or reject 
    *transaction-invocations* from **client nodes**.
    * **Orderer nodes** : nodes that compose the *Ordering service* used by **clients** and **peers** to communicate.
    This service is responsible for guaranteeing the *total-ordering* of the messages.

More details about these components can be found in [Hyperledger Architecture Summary](docs/architecture/summary.md)


### Transaction flow

A summary of the Hyperledger Transaction flow is :
1. **client** sends a *transaction proposal* to the target **chaincode**'s endorsers
2. **endorsers** simulate the transaction and *endorse* or reject
3. **client** collects enough *endorsements* to satisfy the **chaincode**'s *endorsement policy*
4. *ordering service* *delivers* the endorsed transaction to all **peers** to update **state** and **ledger**

See the illustration below for a visual representation of the common-case transaction flow :

![Common-case transaction flow](https://hyperledger-fabric.readthedocs.io/en/latest/_images/flow-4.png)

More details can be found at [Transaction Flow details](docs/transaction_flow/summary.md) and on the official documentation : 
[Hyperledger Transaction Flow Explained](https://hyperledger-fabric.readthedocs.io/en/latest/txflow.html)

Our Architecture Scheme Using Hyperledger Fabric
https://github.com/pumicerD/blockchain_booking/blob/master/hyperledger/hyper.jpg
