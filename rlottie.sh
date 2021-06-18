cd ./rlottie || exit
mkdir build && cd build || exit
cmake ..
make -j4
sudo make install