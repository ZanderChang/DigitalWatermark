package com.zander.digitalwatermark;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.util.Random;

/**
 * Created by Zander on 7/13/2017.
 */

public class attack {

    public final static double MEAN_FACTOR = 2.0;
    public final static int POISSON_NOISE_TYPE = 2;
    public final static int GAUSSION_NOISE_TYPE = 1;
    public final static  double _mNoiseFactor = 25;

    static Bitmap cutBitmap(Bitmap bitmap) {
        Bitmap cpbitmap = bitmap.copy(bitmap.getConfig(), true);
        // 剪切攻击
        for(int i = 0; i < 400; ++i) {
            for(int j = 0; j < 400; ++j) {
                cpbitmap.setPixel(j, i, Color.WHITE);
            }
        }
        return cpbitmap;
    }

    static Bitmap scaleBitmap(Bitmap bitmap, Boolean isExpand) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap scBitmap = null;
        if (isExpand) {
            scBitmap = Bitmap.createBitmap(width / 2, height / 2, bitmap.getConfig());
            for (int i = 0; i < scBitmap.getWidth(); ++i) {
                for (int j = 0; j < scBitmap.getHeight(); ++j) {
                    scBitmap.setPixel(i, j, bitmap.getPixel(i + width / 4, j + height / 4));
                }
            }
        }
        return scBitmap;
    }


    static Bitmap addSaltAndPepperNoise(Bitmap bitmap, float SNR) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap spBitmap = bitmap.copy(bitmap.getConfig(), true);
        int size = (int) (width * height * (1 - SNR));

        for (int i = 0; i < size; ++i) {
            int row = (int)(Math.random() * height);
            int col = (int)(Math.random() * width);
            spBitmap.setPixel(col, row, (255 << 24) | (255 << 16) | (255 << 8) | 255);
        }

        return spBitmap;
    }

    static Bitmap addPoissonNoise(Bitmap bitmap) {
        return filter(bitmap, POISSON_NOISE_TYPE);
    }

    static Bitmap addGaussin(Bitmap bitmap) {
        return filter(bitmap, GAUSSION_NOISE_TYPE);
    }

    static Bitmap filter(Bitmap src, int noiseType) {
        int width = src.getWidth();
        int height = src.getHeight();
        Bitmap nBitmap = src.copy(src.getConfig(), true);
        Random random = new Random();
        int ta, tr, tg, tb;

        for(int row = 0; row < height; row++) {
            for(int col = 0; col < width; col++) {
                ta = (nBitmap.getPixel(col, row) >> 24) & 0xff;
                tr = (nBitmap.getPixel(col, row) >> 16) & 0xff;
                tg = (nBitmap.getPixel(col, row) >> 8) & 0xff;
                tb = nBitmap.getPixel(col, row) & 0xff;
                if(noiseType == POISSON_NOISE_TYPE) {
                    tr = clamp(addPNoise(tr, random));
                    tg = clamp(addPNoise(tg, random));
                    tb = clamp(addPNoise(tb, random));
                } else if(noiseType == GAUSSION_NOISE_TYPE) {
                    tr = clamp(addGNoise(tr, random));
                    tg = clamp(addGNoise(tg, random));
                    tb = clamp(addGNoise(tb, random));
                }
                nBitmap.setPixel(col, row, (ta << 24) | (tr << 16) | (tg << 8) | tb);
            }
        }

        return nBitmap;
    }

    static int addGNoise(int tr, Random random) {
        int v, ran;
        boolean inRange = false;
        do {
            ran = (int)Math.round(random.nextGaussian()*_mNoiseFactor);
            v = tr + ran;
            // check whether it is valid single channel value
            inRange = (v>=0 && v<=255);
            if (inRange) tr = v;
        } while (!inRange);
        return tr;
    }

    private static int clamp(int p) {
        return p > 255 ? 255 : (p < 0 ? 0 : p);
    }

    static int addPNoise(int pixel, Random random) {
        // init:
        double L = Math.exp(-_mNoiseFactor * MEAN_FACTOR);
        int k = 0;
        double p = 1;
        do {
            k++;
            // Generate uniform random number u in [0,1] and let p ← p × u.
            p *= random.nextDouble();
        } while (p >= L);
        double retValue = Math.max((pixel + (k - 1) / MEAN_FACTOR - _mNoiseFactor), 0);
        return (int)retValue;
    }
}
