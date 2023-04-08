#ifndef LOTTIE_TO_GIF_H
#define LOTTIE_TO_GIF_H

#include "gif.h"
#include <cstdio>
#include <cstdlib>
#include <cstring>
#include <array>
#include <zlib.h>
#include <exception>
#include <unistd.h>
#include <iostream>
#include <rlottie.h>

using namespace std;
using namespace rlottie;

#if defined(MSDOS) || defined(OS2) || defined(WIN32) || defined(__CYGWIN__)
#include <fcntl.h>
#include <io.h>
#define SET_BINARY_MODE(file) setmode(fileno(file), O_BINARY)
#else
#define SET_BINARY_MODE(file)
#endif

#define lz_CHUNK_SIZE 0x4000
#define lz_WINDOWN_BITS 15
#define lz_ENABLE_ZLIB_GZIP 32


#endif /* LOTTIE_TO_GIF_H */