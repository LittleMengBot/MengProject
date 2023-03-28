#!/bin/sh
export PATH=/usr/bin:/usr/local/bin:/bin:/usr/sbin:/sbin
mkdir -p build/natives
cd build/natives
cmake ../..
make