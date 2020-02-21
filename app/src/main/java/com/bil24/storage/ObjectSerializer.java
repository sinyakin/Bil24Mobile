package com.bil24.storage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import android.support.annotation.*;

public class ObjectSerializer {

  @Nullable
  public static String serialize(@NonNull Serializable obj) {
    try {
      ByteArrayOutputStream serialObj = new ByteArrayOutputStream();
      ObjectOutputStream objStream = new ObjectOutputStream(serialObj);
      objStream.writeObject(obj);
      objStream.close();
      return encodeBytes(serialObj.toByteArray());
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  @Nullable
  public static Object deserialize(String str) {
    if (str == null || str.length() == 0) return null;
    try {
      ByteArrayInputStream serialObj = new ByteArrayInputStream(decodeBytes(str));
      ObjectInputStream objStream = new ObjectInputStream(serialObj);
      return objStream.readObject();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  @NonNull
  private static String encodeBytes(byte[] bytes) {
    StringBuilder strBuf = new StringBuilder();
    for (byte aByte : bytes) {
      strBuf.append((char) (((aByte >> 4) & 0xF) + ((int) 'a')));
      strBuf.append((char) (((aByte) & 0xF) + ((int) 'a')));
    }
    return strBuf.toString();
  }

  private static byte[] decodeBytes(String str) {
    byte[] bytes = new byte[str.length() / 2];
    for (int i = 0; i < str.length(); i += 2) {
      char c = str.charAt(i);
      bytes[i / 2] = (byte) ((c - 'a') << 4);
      c = str.charAt(i + 1);
      bytes[i / 2] += (c - 'a');
    }
    return bytes;
  }
}