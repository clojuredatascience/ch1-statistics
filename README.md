# Introduction

Example code for chapter one, [Clojure for Data Science](https://www.packtpub.com/big-data-and-business-intelligence/clojure-data-science).

## Data

The data for this chapter has been sourced by the Complex Systems Research Group at the Medical University of Vienna. The analysis we've performed mirrors the research they have undertaken to determine the signals of systematic election fraud, including ballot stuffing. For more information on their research and the methods they employed looking at election data visit their website at http://www.complex-systems.meduniwien.ac.at/elections/election.html or download the paper by Peter Klimeka, Yuri Yegorovb, Rudolf Hanela, and Stefan Thurnerat here: http://www.pnas.org/content/early/2012/09/20/1210722109.full.pdf.

* [UK 2010 election data](http://www.complex-systems.meduniwien.ac.at/elections/ElectionData/UK2010.xls)
* [Russia 2011 election data](http://www.complex-systems.meduniwien.ac.at/elections/ElectionData/Russia2011.zip)

## Instructions

### *nix and OS X

Run the following command-line script to get and unpack the data:

```bash
# Downloads and unzips the data files into this project's /data directory.
    
script/download-data.sh
```

### Windows / manual instructions

  1. Download the data files linked above into this project's data directory
  2. Unzip the Russia2011.zip file
  3. Move Russia2011_1of2.xls and Russia2011_2of2.xls next to UK2010.xls in data directory

## Running examples

Examples can be run with:
```bash
# Replace 1.1 with the example you want to run:

lein run -e 1.1
```
or open an interactive REPL with:

```bash
lein repl
```

## License

Copyright © 2015 Henry Garner

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
