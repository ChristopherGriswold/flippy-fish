package com.iceybones.flippyfish;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class URLConnectionReader {
  public static void main(String[] args) throws Exception {
    URL giverly = new URL("http://www.giverly.org/");
    URLConnection yc = giverly.openConnection();
    BufferedReader in = new BufferedReader(
        new InputStreamReader(yc.getInputStream()));
    String inputLine;

    while ((inputLine = in.readLine()) != null)
      System.out.println(inputLine);
    in.close();
  }
}
