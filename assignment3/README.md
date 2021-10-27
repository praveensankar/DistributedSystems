# Assignment 3

## Running the project

### With `ant`
If you have `ant` installed, in the terminal simply navigate to where the `build.xml` file is located (the `peersim-1.0.5` folder), and run:

```
ant run
```
This will run the project with the configuration file `scripts/RandomExample.txt`.

To run with another configuration file, for example `scripts/ShuffleExample.txt`, simply run:

```
ant run -Dconfig=<filename>
```

## The file structure

### The scripts
The configuration files are located in the folder `scripts`.

### The jar files
The two jar files necessary for running peersim are located in the folder `jars`. This folder is also where the executable jar file `PeerToPeer.jar` for the project is located.

### The source files
The source files developed by us are located in the `src/p2p/` folder. The rest of the files belong to the developers of PeerSim.
