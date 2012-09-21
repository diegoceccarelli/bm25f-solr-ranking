/**
 *  Copyright 2011 Diego Ceccarelli
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
package eu.europeana.assets.service.ir.text.cli;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import eu.europeana.assets.service.ir.text.util.IOUtils;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

/**
 * AbstractCommandLineInterface.java
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 09/ott/2011
 */

public class AbstractCommandLineInterface {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger
			.getLogger(AbstractCommandLineInterface.class);

	public static final String INPUT = "input";
	public static final String OUTPUT = "output";
	protected Map<String, String> params = new HashMap<String, String>();

	private String inputFile, outputFile;
	protected String usage = "java -cp $jar " + this.getClass()
			+ " -input fileinput -output fileoutput";
	private BufferedReader is;
	private BufferedWriter out;
	private String[] args;

	public AbstractCommandLineInterface() {

	}

	public AbstractCommandLineInterface(String[] args) {
		this();
		if (args == null) {
			System.err.println(usage);
			System.exit(-1);
		}
		params.put(INPUT, "");
		params.put(OUTPUT, "");
		this.args = args;
		getParams();
	}

	public AbstractCommandLineInterface(String[] args, String[] parameters) {
		this();
		if (args == null) {
			System.err.println(usage);
			System.exit(-1);
		}
		this.args = args;
		for (String p : parameters)
			params.put(p, "");
		getParams();
	}

	public AbstractCommandLineInterface(String[] args, String[] parameters,
			String usage) {
		this();
		if (args == null) {
			System.err.println(usage);
			System.exit(-1);
		}
		this.args = args;
		this.usage = usage;
		for (String p : parameters)
			params.put(p, "");
		getParams();
	}

	protected String getInput() {
		return inputFile;
	}

	protected String getOutput() {
		return outputFile;
	}

	protected String getParam(String param) {
		return params.get(param);
	}

	protected void getParams() {
		OptionParser parser = new OptionParser();
		for (String p : params.keySet()) {
			parser.accepts(p).withRequiredArg();
		}
		OptionSet option = parser.parse(args);
		for (String p : params.keySet()) {
			if (option.has(p)) {
				params.put(p, (String) option.valueOf(p));
			} else {
				System.err.println(usage);
				System.exit(-1);
			}
		}
		if (params.containsKey(INPUT))
			inputFile = params.get(INPUT);
		if (params.containsKey(OUTPUT))
			outputFile = params.get(OUTPUT);

	}

	protected void openInput() throws IOException {
		try {
			is = new BufferedReader(new FileReader(inputFile));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			throw new IOException(
					"Error opening the input file, file does not exist");
		}
	}

	protected void openOutput() {
		out = IOUtils.getPlainOrCompressedUTF8Writer(outputFile);
	}

	protected String readLineFromInput() throws IOException {
		if (is == null)
			throw new IOException("Error reading from the input file");
		return is.readLine();
	}

	protected void writeLineInOutput(String line) {
		if (out == null) {
			logger.error("Error writing the line " + line
					+ " in the outputFile ");
			System.exit(-1);
		}
		try {
			out.write(line);

			out.write("\n");
		} catch (IOException e) {
			logger.error("Error writing the line " + line
					+ " in the outputFile ");
			System.exit(-1);
		}
	}

	protected void writeInOutput(String str) throws IOException {
		if (out == null)
			throw new IOException("Error writing the string " + str
					+ " in the outputFile ");
		out.write(str);
	}

	protected void openInputAndOutput() throws IOException {
		getInput();
		getOutput();
		openInput();
		openOutput();
	}

	protected void closeInput() throws IOException {
		try {
			is.close();
		} catch (IOException e) {
			throw new IOException("Error closing the input file");
		}
	}

	protected void closeOutput()  {
		try {
			out.close();
		} catch (IOException e) {
			logger.error("closing the output file");
			System.exit(-1);
		}
	}

	protected void closeInputAndOuput() throws IOException {
		closeInput();
		closeOutput();
	}

	public String getUsage() {
		return usage;
	}

	public void setUsage(String usage) {
		this.usage = usage;
	}
}
