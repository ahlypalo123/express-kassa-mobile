package com.hlypalo.express_kassa.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class CheckPrinterWriter {

    private static final String hexStr = "0123456789ABCDEF";
    private static final String[] binaryArray = { "0000", "0001", "0010", "0011",
            "0100", "0101", "0110", "0111", "1000", "1001", "1010", "1011",
            "1100", "1101", "1110", "1111" };

    public static byte[] FEED_LINE = {10};
    public static final byte[] ESC_ALIGN_CENTER = new byte[] { 0x1b, 'a', 0x01 };

    private final Context context;
    private final OutputStream os;
    private boolean first = true;

    public CheckPrinterWriter(Context context, OutputStream os) {
        this.context = context;
        this.os = os;
    }

    public void writeLineBreak() {
        try {
            os.write(FEED_LINE);
        } catch (IOException e) {
            Log.e("PrintTools", "error occurred while executing FEED_LINE", e);
        }
    }

    public void writeLine(String right, String left) {
        Bitmap bmp1 = textToImage(right, Gravity.START);
        Bitmap bmp2 = textToImage(left, Gravity.END);
        writeImage(overlay(bmp1, bmp2));
    }

    public void writeLineBold(String right, String left) {
        Bitmap bmp1 = textToImage(right, Gravity.START, true);
        Bitmap bmp2 = textToImage(left, Gravity.END, true);
        writeImage(overlay(bmp1, bmp2));
    }

    public void writeLine(String line, Integer gravity) {
        Bitmap bmp = textToImage(line, gravity);
        writeImage(bmp);
    }

    private void writeImage(Bitmap bmp) {
        byte[] command = decodeBitmap(bmp);
        try {
            if (first) {
                os.write(ESC_ALIGN_CENTER);
                first = false;
            }
            os.write(command);
        } catch (Exception e) {
            Log.e("PrintTools", "error occurred while reading line", e);
        }
    }

    private Bitmap textToImage(String text, Integer gravity) {
        return textToImage(text, gravity, false);
    }

    private Bitmap textToImage(String text, Integer gravity, boolean bold) {
        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setDrawingCacheEnabled(true);
        textView.setGravity(gravity);
        if (bold) {
            textView.setTypeface(null, Typeface.BOLD);
        }
        textView.buildDrawingCache();
        textView.measure(View.MeasureSpec.makeMeasureSpec(300, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        textView.layout(0, 0, textView.getMeasuredWidth(), textView.getMeasuredHeight());
        return textView.getDrawingCache();
    }

    private static Bitmap overlay(Bitmap src, Bitmap dst) {
        final int width = src.getWidth();
        final int height = src.getHeight();
        final int size = width * height;

        int[] pixSrc = new int[size];
        src.getPixels(pixSrc, 0, width, 0, 0, width, height);

        int[] pixDst = new int[size];
        dst.getPixels(pixDst, 0, width, 0, 0, width, height);

        int[] pixOverlay = new int[size];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int index = y * width + x;

                if (pixSrc[index] != 0) {
                    pixOverlay[index] = pixSrc[index];
                }
                if (pixDst[index] != 0) {
                    pixOverlay[index] = pixDst[index];
                }

            }
        }
        return Bitmap.createBitmap(pixOverlay, width, height,
                Bitmap.Config.ARGB_4444);
    }

    private byte[] decodeBitmap(Bitmap bmp){
        int bmpWidth = bmp.getWidth();
        int bmpHeight = bmp.getHeight();

        List<String> list = new ArrayList<String>(); //binaryString list
        StringBuffer sb;


        int bitLen = bmpWidth / 8;
        int zeroCount = bmpWidth % 8;

        String zeroStr = "";
        if (zeroCount > 0) {
            bitLen = bmpWidth / 8 + 1;
            for (int i = 0; i < (8 - zeroCount); i++) {
                zeroStr = zeroStr + "0";
            }
        }

        for (int i = 0; i < bmpHeight; i++) {
            sb = new StringBuffer();
            for (int j = 0; j < bmpWidth; j++) {
                int color = bmp.getPixel(j, i);

                if (color == 0)
                    sb.append("0");
                else
                    sb.append("1");
            }
            if (zeroCount > 0) {
                sb.append(zeroStr);
            }
            list.add(sb.toString());
        }

        List<String> bmpHexList = binaryListToHexStringList(list);
        String commandHexString = "1D763000";
        String widthHexString = Integer
                .toHexString(bmpWidth % 8 == 0 ? bmpWidth / 8
                        : (bmpWidth / 8 + 1));
        if (widthHexString.length() > 2) {
            Log.e("decodeBitmap error", " width is too large");
            return null;
        } else if (widthHexString.length() == 1) {
            widthHexString = "0" + widthHexString;
        }
        widthHexString = widthHexString + "00";

        String heightHexString = Integer.toHexString(bmpHeight);
        if (heightHexString.length() > 2) {
            Log.e("decodeBitmap error", " height is too large");
            return null;
        } else if (heightHexString.length() == 1) {
            heightHexString = "0" + heightHexString;
        }
        heightHexString = heightHexString + "00";

        List<String> commandList = new ArrayList<String>();
        commandList.add(commandHexString+widthHexString+heightHexString);
        commandList.addAll(bmpHexList);

        return hexList2Byte(commandList);
    }

    private  byte[] hexList2Byte(List<String> list) {
        List<byte[]> commandList = new ArrayList<byte[]>();

        for (String hexStr : list) {
            commandList.add(hexStringToBytes(hexStr));
        }
        byte[] bytes = sysCopy(commandList);
        return bytes;
    }

    private  byte[] sysCopy(List<byte[]> srcArrays) {
        int len = 0;
        for (byte[] srcArray : srcArrays) {
            len += srcArray.length;
        }
        byte[] destArray = new byte[len];
        int destLen = 0;
        for (byte[] srcArray : srcArrays) {
            System.arraycopy(srcArray, 0, destArray, destLen, srcArray.length);
            destLen += srcArray.length;
        }
        return destArray;
    }

    private  byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    private  byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    private  List<String> binaryListToHexStringList(List<String> list) {
        List<String> hexList = new ArrayList<String>();
        for (String binaryStr : list) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < binaryStr.length(); i += 8) {
                String str = binaryStr.substring(i, i + 8);

                String hexString = myBinaryStrToHexString(str);
                sb.append(hexString);
            }
            hexList.add(sb.toString());
        }
        return hexList;

    }

    private  String myBinaryStrToHexString(String binaryStr) {
        String hex = "";
        String f4 = binaryStr.substring(0, 4);
        String b4 = binaryStr.substring(4, 8);
        for (int i = 0; i < binaryArray.length; i++) {
            if (f4.equals(binaryArray[i]))
                hex += hexStr.substring(i, i + 1);
        }
        for (int i = 0; i < binaryArray.length; i++) {
            if (b4.equals(binaryArray[i]))
                hex += hexStr.substring(i, i + 1);
        }

        return hex;
    }
}