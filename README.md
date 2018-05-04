# MicroUTS

Scaffolding for MicroRTS AI development. This repository will be used for developing an AI and testing it against MicroRTS built-in AI as well as other open source agents.

## Installation

Make sure to include the `microrts.jar` library located in `lib` for access to MicroRTS.

### Third Party AI

This repo includes a few third party AI for comparison and training:

- [MicroRTS](https://github.com/nbarriga/microRTSbot)
- [SCV](https://github.com/rubensolv/SCV)

The compiled `.jar` files for each can be found in the `lib` directory

## Usage

The primary goal of this scaffolding is to easily run AI simulations from the command line using MicroRTS. Currently this uses simple unnamed command line arguments, however this will be improved later to use configuration files.

The current format for the command line arguments is:

`microuts.jar botfile.txt mapsfile.txt resultsfile.txt iterations (specificAI) (traceDir)`

where:

- `botfile.txt` is a named list of bots to test, separated by new lines. Every permutation (not combination) of these bots are simulated: that is, Bot 0 vs Bot 1 and Bot 1 vs Bot 0 are simulated.
- `mapsfile.txt` is a list of map filenames to use in testing, separated by new lines
- `resultsfile.txt` is the output location of the tests
- `iterations` is an integer argument specifying how many times each match should be repeated
- `(specificAI)` is an optional integer argument to restrict simulations to only the chosen AI. i.e., `specificAI=0` results in only the matches involving the first bot in the `botfile.txt` will be simulated
- `(traceDir)` is an optional output directory to save traces to, which can be viewed by the MicroRTS GUI frontend

### Remote AI Usage

Remote agents can be used by specifying a `SocketAI` in the `botfile` as so:

`SocketAI <ip|hostname> <port>`

which will create a new connection to the specified `ip:port` or `hostname:port` location each time the specified AI has a match. Multiple `SocketAI` can be used, pointing to different servers. The server should be capable of multiple connections as the experimenter clones the AI a lot, which causes many connections to the server.