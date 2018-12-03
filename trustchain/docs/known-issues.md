# Known issues

## Setup

### IPV8

#### Installing Twisted and Netifaces packages on Windows

According to [python-binance - Issue #148](https://github.com/sammchardy/python-binance/issues/148),
simply downlad the right **wheels** from :
1. [Unofficial Windows Binaries for Python Extension Packages - Twisted](https://www.lfd.uci.edu/~gohlke/pythonlibs/#twisted)
2. [Unofficial Windows Binaries for Python Extension Packages - Netifaces](https://www.lfd.uci.edu/~gohlke/pythonlibs/#netifaces)
3. Install the wheels :\
`python -m pip install <path_to_twisted_wheel>`\
`python -m pip install <path_to_netifaces_wheel>`

## Running tests

### libsodium on Windows

Tests might fail with

`OSError: Couldn't locate nacl lib, searched for libsodium`

This is resolved by the following:
1. Download a [libsodium release](https://download.libsodium.org/libsodium/releases/)
2. Go to a dir in the zip like ./x64/Release/dynamic/v141
3. Copy libsodium.dll to C:/Windows/System32

### win32api module

`At test: ipv8/test/REST/test_attestation_endpoint.py:TestAttestationEndpoint:
Problem: ModuleNotFound`\
`Error: No module named 'win32api'`

Fix : `pip install pypiwin32`

### module pyipv8 not found

If running some of the [overlay tutorial](https://github.com/Tribler/py-ipv8/blob/master/doc/overlay_tutorial.md) scripts fail with

`no module named pyipv8.ipv8.some_module` or `no module named pyipv8.ipv8_service`

you can add the top-level folder (the one where you create `__init__.py` and `main.py`) to the Python path.\
Insert the following at the top of `main.py` :

`from os.path import abspath, dirname`\
`from sys import path`\
`path.insert(0, abspath(dirname(__file__)))`

