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

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;



import eu.europeana.assets.service.ir.text.domain.AverageFieldLengths;

/**
 * GetAverageFieldsLength.java iterate over a solr index
 * and retrieve the average length in terms of each field.
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 09/ott/2011
 */

public class GetAverageFieldLengths extends AbstractCommandLineInterface {

	private static Logger logger = Logger.getLogger(GetAverageFieldLengths.class);
	protected static String usage = "java -cp $jar "
			+ GetAverageFieldLengths.class
			+ " -input solrindex";

	protected static String[] params = new String[] { INPUT };

	public GetAverageFieldLengths(String[] args) {
		super(args);
	}

	public GetAverageFieldLengths(String[] args, String[] parameters, String usage) {
		super(args, parameters, usage);
	}

	public static void main(String[] args) {

		GetAverageFieldLengths cli = new GetAverageFieldLengths(args, params, usage);
		String solrIndex =  cli.getInput();
		
		AverageFieldLengths fieldLengths = new AverageFieldLengths(solrIndex);
		try {
			fieldLengths.load(-1);
		} catch (IOException e) {
			logger.error("reading the field lengths");
			System.exit(-1);
		}
		System.out.println(fieldLengths);

		System.exit(0);

	}
}