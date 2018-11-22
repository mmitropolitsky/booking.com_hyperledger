# blockchain_booking

## Setup

### [Optional] Python virtual environment

You can use [Anaconda](https://conda.io/docs/index.html) in order to setup a python virtual environment for the project :

1. [Install the requirements](https://conda.io/docs/user-guide/install/index.html#system-requirements)
    TL;DR :
    * Python 2.7, 3.4, 3.5 or 3.6
    * `pip install pycosat PyYAML requests`
2. [Install Anaconda](https://conda.io/docs/user-guide/install/index.html#regular-installation)
3. Clone this repo : `git clone https://github.com/pumicerD/blockchain_booking.git`
4. [Create the virtual environment](https://conda.io/docs/user-guide/tasks/manage-environments.html#creating-an-environment-from-an-environment-yml-file)
    TL;DR : `conda env create -f environment.yml`
5. Activate the virtual environment :
    * Windows : `activate trustchain`
    * Linux and macOS : `source activate trustchain`

### IPv8

Clone the repo : `git clone https://github.com/Tribler/py-ipv8`

Install dependencies (not necessary if you used the virtual environment YAML config file) :
`pip install --upgrade -r requirements.txt`
`pip install nose coverage yappi service_identity`

Install the module :
`pip install -e py-ipv8/`

Run tests : 
    * Windows : `py-ipv8/run_all_tests_windows.bat`
    * Linux : `py-ipv8/run_all_tests_unix.sh`

### Run the overlay tutorial

Follow the steps of the [overlay tutorial](https://github.com/Tribler/py-ipv8/blob/master/doc/overlay_tutorial.md)

*Note* : with the provided I had errors where pyipv8 is not recognized as a module, you can copy `ipv8_service.py` to the top-level folder (the one where you create `__init__.py` and `main.py`) and mangle the import statements :
`from pyipv8.ipv8_service import something` => `from ipv8_service import something`
`from ipv8.some_submodule import something` => `from ipv8.some_submodule import something`