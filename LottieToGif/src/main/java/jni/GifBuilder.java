package jni;

public class GifBuilder {
    static {
        if (!LibraryLoader.load(GifBuilder.class, "lottie2gif"))
            System.loadLibrary("lottie2gif");
    }

    public native String generateGif(String tgsPath);
}