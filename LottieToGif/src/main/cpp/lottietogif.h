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

typedef uint8_t byte;

struct byte_buffer {
    byte * buffer = nullptr;
    size_t size = 0;
};

struct file {
    FILE * file_pointer = nullptr;
    char * path = nullptr;
};

#define bb_init() {.buffer = NULL, .size = 0}

int bb_append(byte_buffer * bb, byte * data, size_t data_size) {

    bb->buffer = (byte *) realloc(bb->buffer, (bb->size + data_size) * sizeof (byte));
    if (bb->buffer == nullptr) {
        perror("Unable to extend byte buffer");
        return EXIT_FAILURE;
    }
    memset(bb->buffer + bb->size, 0, data_size);
    memcpy(bb->buffer + bb->size, data, data_size);
    bb->size += data_size;
    return EXIT_SUCCESS;
}

#define file_init(_fp_, _path_) { .file_pointer = (_fp_), .path = (_path_) }
#define file_close(_file_) { if ((_file_).file_pointer != NULL) fclose((_file_).file_pointer); }

#endif /* LOTTIE_TO_GIF_H */