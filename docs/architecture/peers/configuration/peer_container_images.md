# Peer Nodes Container Images

**Peer nodes** are Docker containers running the `hyperledger/fabric-peer` image.

## Docker Image

The details of this Docker image can be found in its [Dockerfile](https://github.com/hyperledger/fabric/blob/release-1.3/images/peer/Dockerfile.in).

## Running the Peer

**Peer nodes** are typically run by calling `docker-compose up` on a [docker compose configuration file](#docker-compose-settings).

The main entrypoint is the `peer` command. \
1. Typically a **peer** executes `peer node start` when it is created. \
2. Installing a **chaincode** on the **peer** can then be achieved with : \
 `peer chaincode install someArgs`
3. An installed **chaincode** needs to be *instantiated* before transactions can be invoked on it : \
`peer chaincode instantiate someArgs`

For more details see [peer command reference](https://hyperledger-fabric.readthedocs.io/en/release-1.3/commands/peercommand.html).

## Docker Compose settings

They are exposed as [Docker compose services](https://docs.docker.com/compose/compose-file/#service-configuration-reference) to the network. \
There are three *composer configuration files* defining these services :
1. `base/peer-base.yaml` defines the basic **peer** service settings common to all **peer nodes** : \
    * base image to create the container from (usually `hyperledger/fabric-peer`)
    * environment variables :
        * CORE_VM_ENDPOINT - *not sure what this does exactly*
        * CORE_VM_DOCKER_HOSTCONFIG_NETWORKMODE - *not sure what this does exactly* \
                # the previous two setting starts chaincode containers on the same bridge network as the peers
                https://docs.docker.com/compose/networking/
        * CORE_LOGGING_LEVEL - the logging level
        * CORE_PEER_TLS_ENABLED - enable TLS protocol
        * CORE_PEER_GOSSIP_USELEADERELECTION - whether or not to elect a leader for the Gossip protocol
        * CORE_PEER_GOSSIP_ORGLEADER - whether or not to use an appointed *organization leader* for the Gossip protocol
        * CORE_PEER_PROFILE_ENABLED - *not sure exactly what this does*
        * CORE_PEER_TLS_CERT_FILE - the certificate used during the TLS handshake
        * CORE_PEER_TLS_KEY_FILE - the peer's private key file used during the TLS handshake
        * CORE_PEER_TLS_ROOTCERT_FILE - the root certificate used to establish the [chain of trust](https://en.wikipedia.org/wiki/Root_certificate)
    * default command (`peer node start`)
2. `base/docker-compose-base.yaml` defines  the different **peer** services for every *organization* in the network (one
service per **peer** node in the *organization) : \
    * each service **extends** the basic service defined in `peer-base.yaml`
    * environment variables :
        * CORE_PEER_ID - peer node ID
        * CORE_PEER_ADDRESS - peer node address
        * CORE_PEER_LOCALMSPID - peer's organization [Membership Service Provider](https://hyperledger-fabric.readthedocs.io/en/release-1.3/membership/membership.html) ID
        * environment variables defined in `peer-base.yaml` can be overriden here if necessary 
    * [volume mount points](https://docs.docker.com/storage/volumes/)
    * exposed ports : \
            # note that the `hyperledger/fabric-peer` image defines the listening ports 7051 and 7053, however if multiple
            peers are meant to run on the same host, they need to map to different ports on the host. See the config file
            for an example with two peer nodes.
3. `docker-compose.yaml` defines the network(s) settings for each service (one per peer node) :
    * define references to the volumes containing network authentication certificates
    * define the network(s) existing in the system
    * each service extends a service defined in `base/docker-compose-base.yaml`
    * attach each service to one (or more ?) network 