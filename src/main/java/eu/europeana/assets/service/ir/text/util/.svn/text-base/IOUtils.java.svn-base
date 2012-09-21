/**
 *  Copyright 2012 Diego Ceccarelli
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package eu.europeana.assets.service.ir.text.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * IOUtils.java
 *
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it
 * created on 25/gen/2012
 */
public class IOUtils {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(IOUtils.class);



	public static BufferedReader getPlainOrCompressedReader(String file) {
		BufferedReader br = null;
		try {
			if (file.endsWith(".gz")) {
				br = new BufferedReader(new InputStreamReader(
						new GZIPInputStream(new FileInputStream(file))));
			} else {
				br = new BufferedReader(new InputStreamReader(
						new FileInputStream(file)));
			}
		} catch (IOException e) {
			logger.error("opening the file {} ({})", file, e.toString());
			System.exit(-1);
		}
		return br;
	}
	
	
	public static BufferedReader getPlainOrCompressedUTF8Reader(String file) {
		BufferedReader br = null;
		try {
			if (file.endsWith(".gz")) {
				br = new BufferedReader(new InputStreamReader(
						new GZIPInputStream(new FileInputStream(file)),"UTF8"));
			} else {
				br = new BufferedReader(new InputStreamReader(
						new FileInputStream(file),"UTF8"));
			}
		} catch (IOException e) {
			logger.error("opening the file {} ({})", file, e.toString());
			System.exit(-1);
		}
		return br;
	}
	

	public static BufferedWriter getPlainOrCompressedWriter(String file) {
		BufferedWriter bw = null;
		try {
			if (file.endsWith(".gz")) {
				bw = new BufferedWriter(new OutputStreamWriter(
						new GZIPOutputStream(new FileOutputStream(file))));
			} else {
				bw = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(file)));
			}
		} catch (IOException e) {
			logger.error("opening the file {} ({})", file, e.toString());
			System.exit(-1);
		}
		
		return bw;
	}
	
	public static BufferedWriter getPlainOrCompressedUTF8Writer(String file) {
		BufferedWriter bw = null;
		try {
			if (file.endsWith(".gz")) {
				bw = new BufferedWriter(new OutputStreamWriter(
						new GZIPOutputStream(new FileOutputStream(file)),"UTF8"));
			} else {
				bw = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(file),"UTF8"));
			}
		} catch (IOException e) {
			logger.error("opening the file {} ({})", file, e.toString());
			System.exit(-1);
		}
		
		return bw;
	}
	
	
	
}
