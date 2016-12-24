package com.converter.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.converter.Convert;

public class Test {
	public static void main(String[] args) {
		InputStream is = Test.class.getResourceAsStream("/json-data.txt");
		if(is!=null) {
			BufferedReader br = null;
			try {
				br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				String s = "";
				StringBuffer sb = new StringBuffer();
				while((s=br.readLine())!=null) {
					sb.append(s);
				}
				System.out.println(Convert.createBsonObj(sb.toString()));
			} catch (IOException e) {
				System.out.println("Exception while trying to read the file");
				e.printStackTrace();
			} finally {
				if(br!=null) {
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			System.err.println("Could not load the File specified. Please check the path");
		}
	}
}
