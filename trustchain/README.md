# blockchain_booking

## Setup

[Install the requirements](https://conda.io/docs/user-guide/install/index.html#system-requirements)\
TL;DR :
* [Python 2.7, 3.4, 3.5 or 3.6](https://www.python.org/downloads/)

### Visual Studio

If working with Visual Studio, install [C++ Build Tools for Visual Studio](https://visualstudio.microsoft.com/downloads/)

### \[Optional\] Python virtual environment

You can setup a virtual environment to easily manage the interpreter version, packages and module to use for this project
without affecting the rest of the system. There are several solutions to setup such an environment :

#### Anaconda

You can use [Anaconda](https://conda.io/docs/index.html) in order to setup a python virtual environment for the project :

1. Install dependencies : `pip install pycosat PyYAML requests`
2. [Install Anaconda](https://conda.io/docs/user-guide/install/index.html#regular-installation)
3. Clone this repo : `git clone https://github.com/pumicerD/blockchain_booking.git`
4. Go to the cloned directory
5. [Create the virtual environment](https://conda.io/docs/user-guide/tasks/manage-environments.html#creating-an-environment-from-an-environment-yml-file)
using the environment config file\
TL;DR : `conda env create -f environment.yml`
6. Activate the virtual environment :
    * Windows : `activate trustchain`
    * Linux and macOS : `conda activate trustchain`

#### Pyenv

See documentation for [Pyenv](https://github.com/pyenv/pyenv-virtualenv)

#### Virtualenv wrapper

See documentation for [Virtualenv wrapper](https://pypi.org/project/virtualenvwrapper/)

#### Using a virtual environment with PyCharm

1. Import the project
2. Go to *File > Settings*
3. Expand the project's entry in the left menu
4. Under *Project Interpreter*, click the settings icon next to the name of the current interpreter (default)
5. Select *Add*
6. Select your virtual environment (virtualenv, conda, etc ...)

### IPv8

Clone the repo : `git clone https://github.com/Tribler/py-ipv8.git pyipv8`

Install dependencies (not necessary if you used the virtual environment YAML config file) :

`pip install --upgrade -r requirements.txt`\
`pip install nose coverage yappi service_identity`

*Note* : there might be issues installing Netifaces and Twisted on Windows. See [Known Issues](docs/known-issues.md)

Run tests :
* Windows : `pyipv8/run_all_tests_windows.bat`
* Linux : `pyipv8/run_all_tests_unix.sh`

*Note* : there might be issues about *libnacl* and/or *libsodium* not being found. See [Known Issues](docs/known-issues.md)

### Run the overlay tutorial

Follow the steps of the [overlay tutorial](https://github.com/Tribler/py-ipv8/blob/master/doc/overlay_tutorial.md)

*Note* : if encountering errors with the import statements, see [Known Issues](docs/known-issues.md)
