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

`microuts.jar run.properties resultsfile.txt (traceDir)`

where:

- `run.properties` is a configuration properties file setting all the parameters for the run:
  - `bot_file` is a named list of bots to test, separated by new lines. Every permutation (not combination) of these bots are simulated: that is, Bot 0 vs Bot 1 and Bot 1 vs Bot 0 are simulated.
  - `map_file` is a list of map filenames to use in testing, separated by new lines
  - `iterations` is an integer specifying how many times each match should be repeated
  - `specific_AI` is an optional integer to restrict simulations to only the chosen AI. i.e., `specificAI=0` results in only the matches involving the first bot in the `bot_file` will be simulated. Set to `-1` to disable
  - `max_cycles` is an integer specifying how many cycles each game should go for before declaring a tie
  - `partially_observable` is false if players have full vision of the map
  - `UTT_version` specifies the Unit Type Table version:
    - `1` = Original
    - `2` = Original Fine-tuned
    - `3` = Non-deterministic version of `2` (damages are random)
  - `conflict_policy` specifies how move conflicts are handled (See [here](https://github.com/santiontanon/microrts/wiki/Game-Definition#actions)):
    - `1` = Cancel both moves
    - `2` = Cancel one (non-deterministic)
    - `3` = Cancel one (deterministic)
  -`skip_self_play` is false if each AI should play against itself
- `resultsfile.txt` is the output location of the tests
- `(traceDir)` is an optional output directory to save traces to, which can be viewed by the MicroRTS GUI frontend

### Remote AI Usage

Remote agents can be used by specifying a `SocketAI` in the `botfile` as so:

`SocketAI <ip|hostname> <port>`

which will create a new connection to the specified `ip:port` or `hostname:port` location each time the specified AI has a match. Multiple `SocketAI` can be used, pointing to different servers. The server should be capable of multiple connections as the experimenter clones the AI a lot, which causes many connections to the server.