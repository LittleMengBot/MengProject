#include "lottietogif.h"
#include "jni_NativeBuilder.h"

class GifBuilder {
public:
    explicit GifBuilder(FILE *builderGifFile, const uint32_t width,
                        const uint32_t height, const int bgColor=0xffffffff, const uint32_t delay = 2)
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
           // compute only if alpha is non zero
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

    int setup(const char *file_name, size_t *width, size_t *height)
    {
        const char *path = file_name;

        *width = *height = 512;   //default gif size

        if (!path) {
            return help(); }

        std::array<char, 5000> memory{};

#ifdef _WIN32
        path = _fullpath(memory.data(), path, memory.size());
#else
        path = realpath(path, memory.data());
#endif
        if (!path){
            return help();
        }

        fileName = std::string(path);

        if (!tgsFile()) {
            return help();
        }

        gifName = basename(fileName);
        gifName = gifName.replace(gifName.find(".tgs"), 4, "");
        gifName.append(".gif");
        return 0;
    }

    int render(byte * in_file_data, FILE *outFile, uint32_t w, uint32_t h)
    {
        string data(reinterpret_cast<char*> (in_file_data));
        auto player = rlottie::Animation::loadFromData(data, "", "", false);
        if (!player) {
            return help();
        }

        auto buffer = std::unique_ptr<uint32_t[]>(new uint32_t[w * h]);
        size_t frameCount = player->totalFrame();

        GifBuilder builder(outFile, w, h, bgColor);
        for (size_t i = 0; i < frameCount ; i++) {
            rlottie::Surface surface(buffer.get(), w, h, w * 4);
            player->renderSync(i, surface);
            builder.addFrame(surface);
        }
        return result();
    }



private:
    static std::string basename(const std::string &str)
    {
        return str.substr(str.find_last_of("/\\") + 1);
    }

    bool tgsFile() {
        std::string extn = ".tgs";
        if ( fileName.size() <= extn.size() ||
             fileName.substr(fileName.size()- extn.size()) != extn )
            return false;

        return true;
    }

    int result() {
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

int un_gzip(FILE * in_file, byte_buffer * out_data) {
    z_stream strm = {nullptr};
    byte in[lz_CHUNK_SIZE];
    byte out[lz_CHUNK_SIZE];
    bool first_read = true;

    strm.zalloc = Z_NULL;
    strm.zfree = Z_NULL;
    strm.opaque = Z_NULL;
    strm.next_in = in;
    strm.avail_in = 0;


    if (inflateInit2(& strm, lz_WINDOWN_BITS | lz_ENABLE_ZLIB_GZIP) < 0) {
        fputs("Unable to init zlib\n", stderr);
        return EXIT_FAILURE;
    }

    while (true) {
        size_t bytes_read;
        int zlib_status;

        bytes_read = fread(in, sizeof (byte), sizeof (in), in_file);
        if (ferror(in_file)) {
            inflateEnd(& strm);
            perror("Unable to read file data\n");
            return EXIT_FAILURE;
        }
        strm.avail_in = bytes_read;
        strm.next_in = in;
        do {
            size_t have;
            strm.avail_out = lz_CHUNK_SIZE;
            strm.next_out = out;
            zlib_status = inflate(& strm, Z_NO_FLUSH);
            switch (zlib_status) {
                case Z_OK:
                case Z_STREAM_END:
                case Z_BUF_ERROR:
                    break;

                case Z_DATA_ERROR:
                    inflateEnd(&strm);
                    if (first_read) {
                        do {
                            if (bb_append(out_data, in, bytes_read) == EXIT_FAILURE) {
                                fputs("Unable to allocate memory\n", stderr);
                                return EXIT_FAILURE;
                            }
                            bytes_read = fread(in, sizeof (byte), sizeof (in), in_file);
                            if (ferror(in_file)) {
                                perror("Unable to read file data\n");
                                return EXIT_FAILURE;
                            }
                        } while (bytes_read > 0);
                        return EXIT_SUCCESS;
                    } else {
                        fputs("zlib data error\n", stderr);
                        return EXIT_FAILURE;
                    }
                default:
                    inflateEnd(& strm);
                    fprintf(stderr, "zlib error %d.\n", zlib_status);
                    return EXIT_FAILURE;
            }
            have = lz_CHUNK_SIZE - strm.avail_out;
            if (bb_append(out_data, out, have) == EXIT_FAILURE) {
                fputs("Unable to allocate memory\n", stderr);
                return EXIT_FAILURE;
            }
        } while (strm.avail_out == 0);
        if (feof(in_file)) {
            inflateEnd(& strm);
            break;
        }
        if (first_read) first_read = false;
    }
    return EXIT_SUCCESS;
}

unsigned get_seed() { std::time_t t; return std::time(&t); }

std::string generate_rand(){
    default_random_engine e(get_seed());
    std::uniform_real_distribution<float> u(1000000, 9999999);
    std::string s;
    s.append("/tmp/");
    s.append(std::to_string(u(e)));
    s.append(".gif");
    return s;
}

std::string generate_gif(const std::string& tgs_path){
    char tmp[256];
    getcwd(tmp, 256);
    App app;
    size_t w, h;

    std::string tempName = generate_rand();
    FILE *temp = fopen(tempName.c_str(), "w+b");

    FILE *in_file = fopen(tgs_path.c_str(), "rb");
    file out_file = file_init(stdout, nullptr);
    byte_buffer in_file_data = bb_init();
    in_file_data.buffer = (byte *) calloc(0, sizeof (byte));
    if (in_file_data.buffer == nullptr) {
        perror("Unable to init byte buffer");
        fclose(in_file);
        file_close(out_file)
        return "";
    }
    int result = un_gzip(in_file, &in_file_data);
    if (result == EXIT_SUCCESS) {
        byte eos[1] = {'\0'};
        bb_append(&in_file_data, eos, 1);
        if (app.setup(tgs_path.c_str(), &w, &h)) return "";

        try{
            app.render(in_file_data.buffer, temp, w, h);
        }catch(exception &e){
            return "";
        }
    }
    else
        fputs("zlib error\n", stderr);

    return tempName;
}

char* jstringToChar(JNIEnv* env, jstring jstr) {
    char* rtn = nullptr;
    jclass clsstring = env->FindClass("java/lang/String");
    jstring strencode = env->NewStringUTF("UTF-8");
    jmethodID mid = env->GetMethodID(clsstring, "getBytes", "(Ljava/lang/String;)[B");
    auto barr = (jbyteArray) env->CallObjectMethod(jstr, mid, strencode);
    jsize alen = env->GetArrayLength(barr);
    jbyte* ba = env->GetByteArrayElements(barr, JNI_FALSE);
    if (alen > 0) {
        rtn = (char*) malloc(alen + 1);
        memcpy(rtn, ba, alen);
        rtn[alen] = 0;
    }
    env->ReleaseByteArrayElements(barr, ba, 0);
    return rtn;
}

JNIEXPORT jstring JNICALL Java_jni_NativeBuilder_generateGif(JNIEnv * env, jobject, jstring tgsPath){
    char *tgs_path = jstringToChar(env, tgsPath);
    std::string tgs(tgs_path);
    const std::string gif_path = generate_gif(tgs);

    if (!gif_path.empty()){
        return env->NewStringUTF(gif_path.c_str());
    }else{
        return env->NewStringUTF("");
    }
}
