package jni;

public class NativeBuilder {
    static {
        if (!LibraryLoader.load(NativeBuilder.class, "webp2png")) {
            System.loadLibrary("webp2png");
        }
    }

    public native byte[] generateGif(byte[] tgsData);

    public native byte[] generatePNGFromWebP(byte[] webp, int webp_size);
}