#include "webptopng.h"
#include "jni_NativeBuilder.h"

void my_png_write_data(png_structp png_ptr, png_bytep data, png_size_t length){
    auto* p=(struct mem_encode*)png_get_io_ptr(png_ptr); /* was png_ptr->io_ptr */
    size_t nsize = p->size + length;

    /* allocate or grow buffer */
    if(p->buffer)
        p->buffer = (char *)realloc(p->buffer, nsize);
    else
        p->buffer = (char *)malloc(nsize);

    if(!p->buffer)
        png_error(png_ptr, "Write Error");

    /* copy new bytes to end of buffer */
    memcpy(p->buffer + p->size, data, length);
    p->size += length;
}

mem_encode makePNGBuffer(const unsigned char *rgba, int width, int height){
    png_structp png_ptr = nullptr;
    png_infop info_ptr =nullptr;
    png_bytep row = nullptr;

    struct mem_encode state = {nullptr, 0};

    // Initialize write structure
    png_ptr = png_create_write_struct(PNG_LIBPNG_VER_STRING, nullptr, nullptr, nullptr);
    if (png_ptr == nullptr) {
        fprintf(stderr, "Could not allocate write struct\n");
        goto finalise;
    }

// Initialize info structure
    info_ptr = png_create_info_struct(png_ptr);
    if (info_ptr == nullptr) {
        fprintf(stderr, "Could not allocate info struct\n");
        goto finalise;
    }

    png_set_write_fn(png_ptr, &state, my_png_write_data, nullptr);

    // Write header (8 bit colour depth)
    png_set_IHDR(png_ptr, info_ptr, width, height,
                 8, PNG_COLOR_TYPE_RGB_ALPHA, PNG_INTERLACE_NONE,
                 PNG_COMPRESSION_TYPE_BASE, PNG_FILTER_TYPE_BASE);

    png_write_info(png_ptr, info_ptr);

    row = (png_bytep) malloc(4 * width * sizeof(png_byte));

    // Write image data
    int x, y;
    for (y=0 ; y<height ; y++) {
        for (x=0 ; x<width ; x++) {
            row[x*4] = rgba[(y*width +x)*4];
            row[x*4+1] = rgba[(y*width +x)*4 + 1];
            row[x*4+2] = rgba[(y*width +x)*4 + 2];
            row[x*4+3] = rgba[(y*width +x)*4 + 3];
        }
        png_write_row(png_ptr, row);
    }

    // End write
    png_write_end(png_ptr, nullptr);

    finalise:
      if (info_ptr != nullptr) png_free_data(png_ptr, info_ptr, PNG_FREE_ALL, -1);
      if (png_ptr != nullptr) png_destroy_write_struct(&png_ptr, (png_infopp)nullptr);
      if (row != nullptr) free(row);

    return state;

}

mem_encode webp_decode(uint8_t * data, size_t data_size){
    WebPDecoderConfig config;
    WebPInitDecoderConfig(&config);
    WebPDecBuffer* const output_buffer = &config.output;
    output_buffer->colorspace = MODE_RGBA;

//    FILE *fp = fopen("/Users/sakura/Downloads/amnoi.png", "wb");

    int width;
    int height;

    WebPGetInfo(data, data_size, &width, &height);
    uint8_t *bf = WebPDecodeRGBA(data, data_size, &width, &height);
    mem_encode result = makePNGBuffer(bf, width, height);

    WebPFree(bf);
//    fwrite(result, data_size, 1, fp);
//
//    fclose(fp);
    printf("1");

    return result;

}

jbyteArray charToJByteArray(JNIEnv *env, unsigned char *buf, int len) {
    jbyteArray array = env->NewByteArray(len);
    env->SetByteArrayRegion(array, 0, len, reinterpret_cast<jbyte *>(buf));
    return array;
}

char *jByteArrayToChar(JNIEnv *env, jbyteArray buf) {
    char *chars = NULL;
    jbyte *bytes;
    bytes = env->GetByteArrayElements(buf, 0);
    int chars_len = env->GetArrayLength(buf);
    chars = new char[chars_len + 1];
    memset(chars, 0, chars_len + 1);
    memcpy(chars, bytes, chars_len);
    chars[chars_len] = 0;
    env->ReleaseByteArrayElements(buf, bytes, 0);
    return chars;
}

JNIEXPORT jbyteArray JNICALL Java_jni_NativeBuilder_generatePNGFromWebP
(JNIEnv *env, jobject, jbyteArray wb, jint webp_size){
    char *webp_bf = jByteArrayToChar(env, wb);
    mem_encode c_result = webp_decode((uint8_t *)webp_bf, (size_t)webp_size);
    return charToJByteArray(env, (unsigned char *)c_result.buffer, c_result.size);
}