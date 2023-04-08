#include "lottietogif.h"
#include "jni_NativeBuilder.h"

class GifBuilder {
public:
    explicit GifBuilder(vector<uint8_t> &builderGifFile, const uint32_t width,
                        const uint32_t height, const unsigned int bgColor=0xffffffff, const uint32_t delay = 2)
    {
        GifBegin(&handle, builderGifFile, width, height, delay);
        bgColorR = (uint8_t) ((bgColor & 0xff0000) >> 16);
        bgColorG = (uint8_t) ((bgColor & 0x00ff00) >> 8);
        bgColorB = (uint8_t) ((bgColor & 0x0000ff));
    }
    ~GifBuilder()
    {
        GifEnd(&handle);
    }
    void addFrame(rlottie::Surface &s, uint32_t delay = 2)
    {
        argbTorgba(s);
        GifWriteFrame(&handle,
                      reinterpret_cast<uint8_t *>(s.buffer()),
                      s.width(),
                      s.height(),
                      delay);
    }
    void argbTorgba(rlottie::Surface &s) const
    {
        auto *buffer = reinterpret_cast<uint8_t *>(s.buffer());
        uint32_t totalBytes = s.height() * s.bytesPerLine();

        for (uint32_t i = 0; i < totalBytes; i += 4) {
           unsigned char a = buffer[i+3];
           // compute only if alpha is non-zero
           if (a) {
               unsigned char r = buffer[i+2];
               unsigned char g = buffer[i+1];
               unsigned char b = buffer[i];

               if (a != 255) { //un premultiply
                   auto r2 = (unsigned char) ((float) bgColorR * ((float) (255 - a) / 255));
                   auto g2 = (unsigned char) ((float) bgColorG * ((float) (255 - a) / 255));
                   auto b2 = (unsigned char) ((float) bgColorB * ((float) (255 - a) / 255));
                   buffer[i] = r + r2;
                   buffer[i+1] = g + g2;
                   buffer[i+2] = b + b2;

               } else {
                 // only swizzle r and b
                 buffer[i] = r;
                 buffer[i+2] = b;
               }
           } else {
               buffer[i+2] = bgColorB;
               buffer[i+1] = bgColorG;
               buffer[i] = bgColorR;
           }
        }
    }

private:
    GifWriter      handle{};
    uint8_t bgColorR, bgColorG, bgColorB;
};

class App {
public:

    int render(vector<uint8_t> &in_file_data, vector<uint8_t> &outBytes, uint32_t w, uint32_t h) const
    {
        std::string data(in_file_data.begin(), in_file_data.end());
        auto player = rlottie::Animation::loadFromData(data, "", "", false);
        if (!player) {
            return help();
        }

        auto buffer = std::unique_ptr<uint32_t[]>(new uint32_t[w * h]);
        size_t frameCount = player->totalFrame();

        GifBuilder builder(outBytes, w, h, bgColor);
        for (size_t i = 0; i < frameCount ; i++) {
            rlottie::Surface surface(buffer.get(), w, h, w * 4);
            player->renderSync(i, surface);
            builder.addFrame(surface);
        }
        return result();
    }



private:

    static int result() {
        return 0;
    }

    static int help() {
        return 1;
    }

private:
    int bgColor = 0x00ffffff;
    std::string fileName;
    std::string gifName;
};


int un_gzip(const std::vector<uint8_t>* in_data, std::vector<uint8_t>* out_data) {
    z_stream strm = {nullptr};
    bool first_read = true;

    strm.zalloc = Z_NULL;
    strm.zfree = Z_NULL;
    strm.opaque = Z_NULL;
    strm.next_in = const_cast<uint8_t*>(in_data->data());
    strm.avail_in = static_cast<uInt>(in_data->size());

    if (inflateInit2(&strm, lz_WINDOWN_BITS | lz_ENABLE_ZLIB_GZIP) < 0) {
        return EXIT_FAILURE;
    }

    while (true) {
        std::vector<uint8_t> out(lz_CHUNK_SIZE);

        strm.avail_out = static_cast<uInt>(out.size());
        strm.next_out = out.data();
        int zlib_status = inflate(&strm, Z_NO_FLUSH);

        switch (zlib_status) {
            case Z_OK:
            case Z_STREAM_END:
            case Z_BUF_ERROR:
                break;

            case Z_DATA_ERROR:
                inflateEnd(&strm);
                if (first_read) {
                    out_data->insert(out_data->end(), in_data->begin(), in_data->end());
                    return EXIT_SUCCESS;
                } else {
                    return EXIT_FAILURE;
                }

            default:
                inflateEnd(&strm);
                throw std::runtime_error("zlib error");
        }

        out_data->insert(out_data->end(), out.begin(), out.begin() + static_cast<long>(out.size()) - strm.avail_out);
        if (strm.avail_out != 0) {
            break;
        }

        first_read = false;
    }

    inflateEnd(&strm);
    return EXIT_SUCCESS;
}

vector<uint8_t> generate_gif(vector<uint8_t>& tgs_bytes){
    char tmp[256];
    getcwd(tmp, 256);
    App app;

    vector<uint8_t> out_data;
    int result = un_gzip(&tgs_bytes, &out_data);
    if (result == EXIT_SUCCESS) {
        try{
            app.render(tgs_bytes, out_data, 512, 512);
        }catch(exception &e){
            return out_data;
        }
    }
    else
        fputs("zlib error\n", stderr);

    return out_data;
}

JNIEXPORT jbyteArray JNICALL Java_jni_NativeBuilder_generateGif(JNIEnv * env, jobject, jbyteArray tgsData){
    jsize length = env->GetArrayLength(tgsData);
    jbyte* byteArrayPtr = env->GetByteArrayElements(tgsData, nullptr);
    vector<uint8_t> data(byteArrayPtr, byteArrayPtr + length);
    const vector<uint8_t> gif_data = generate_gif(data);
    jbyteArray array = env->NewByteArray(static_cast<jsize>(gif_data.size()));
    env->SetByteArrayRegion(array, 0, static_cast<jsize>(gif_data.size()), reinterpret_cast<const jbyte*>(gif_data.data()));
    return array;
}
