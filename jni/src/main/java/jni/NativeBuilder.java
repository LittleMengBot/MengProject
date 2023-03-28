package jni;

public class NativeBuilder {
    static {
        if (!LibraryLoader.load(NativeBuilder.class, "lottie2gif")) {
            System.loadLibrary("lottie2gif");
        }

        if (!LibraryLoader.load(NativeBuilder.class, "webp2png")) {
            System.loadLibrary("webp2png");
        }
    }

    public native String generateGif(String tgsPath);

    public native byte[] generatePNGFromWebP(byte[] webp, int webp_size);
}