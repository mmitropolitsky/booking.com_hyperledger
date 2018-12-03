# Known Issues

## Hyperledger Setup

### Hyperledger Composer

#### Permission denied

The `npm` commands provided by the setup guide **MUST NOT** be run as root (`sudo`).
However on Linux you might run into permission issues : \
`Cannot open file /usr/local/xxx : Permission denied`

The folder `/usr` is by default owned by root and ensures sudo rights are required to write to it. \
A workaround is to change the ownership of the sub-folder `/usr/local` which is meant for user-only installs : \
`sudo chown -R $USER /usr/local`

#### Error while installing Composer CLI

If you get errors similar to the output pasted in [Error while installing Composer CLI](https://stackoverflow.com/questions/53531073/error-while-installing-hyperledger-composer)
then you might be using an unsupported version of Node.

The required version is 8.x

If using snap to install and manage Node : \
`sudo snap remove node` \
`sudo snap install node --classic --channel=8`

Else, figure out how to uninstall your current Node version and install 8.x :-)