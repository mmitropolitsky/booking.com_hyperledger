# Hyperledger Transaction Flow

The sequence to operate a transaction is :

1. **client** broadcasts a `PROPOSE` message to set of **endorsers** of its choice (can be all) \
A `PROPOSE` message contains , among other things, the ID of the target **chaincode**, the function called, its parameters,
the client's signature.
2. upon receipt of a `PROPOSE` message, the **endorsers** perform a *transaction simulation*, generating the set of KVs
read during the transaction execution (*read-dependencies* or *readset*) and the set of transformations generated : either
a set of new KVs or a set of deltas between old KVs and new KVs (*write-dependencies* or *writeset*)
3. **endorsers** generate a *transaction proposal* `TRAN-PROPOSAL` and forward it *internally* to their *endorsing-logic* 
"layer".
2.b the *endorsing-logic* accepts the *transaction proposal* and sends a `TRANSACTION-ENDORSED` message back to the **client**;
 or rejects the *transaction* and may return `TRANSACTION-INVALID`
4. **client** waits until it collects enough `TRANSACTION-ENDORSED` to satisfy the *endorsement policy* of the target 
**chaincode**.
5. when the *transaction* is endorsed, the **client** calls the *ordering service* `broadcast` functionality to broadcast
the endorsed *transaction*
6. when **peers** receive an endorsed *transaction* through the *ordering service* `deliver` functionality, they check
the *endorsement* fields against the **chaincode**'s *endorsement policy*. Then they check if the *transaction's read-dependencies*
are valid in the current **state** (i.e. the KVs used in the simulation were not modified in the meantime). \
If these checks hold, the *write-dependencies* are written to the **state** and the *transaction* appended to the **ledger**.

## Endorsement Policy

A few examples of *endorsement policies* with a set of **endorsers** (A, B, C, D, E) :
* a valid signature from all **endorsers**
* a valid signature from any one of the **endorsers**
* a valid signature from at least 3-out-of-5 **endorsers**
* give A and C a weight of 25% each and 15% to the others ; require a valid signature with at least 40% weight
