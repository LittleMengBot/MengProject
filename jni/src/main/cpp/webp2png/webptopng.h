#ifndef WEBP_TO_PNG_H
#define WEBP_TO_PNG_H

#include <webp/decode.h>
#include <png.h>
#include <fstream>
#include <cstdio>
#include <cstring>

struct mem_encode{
    char *buffer;
    size_t size;
};

void my_png_write_data(png_structp png_ptr, png_bytep data, png_size_t length);

mem_encode makePNGBuffer(const unsigned char *rgba, int width, int height, size_t *outsize);

mem_encode webp_decode(uint8_t * data, size_t data_size);

#endif /* WEBP_TO_PNG_H */
