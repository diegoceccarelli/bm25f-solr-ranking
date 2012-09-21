/*
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
 *
 */
package eu.europeana.assets.service.ir.text.domain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.NIOFSDirectory;

/**
 * Computes the average lengths of the fields in a solr index.
 * 
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 */

public class AverageFieldLengths {
	private static Logger logger = Logger.getLogger(AverageFieldLengths.class);

	// public static String[] fields = new String[] { "COMPLETENESS", "COUNTRY",
	// "DATA_PROVIDER", "LANGUAGE", "PROVIDER", "TYPE", "date",
	// "dc_date", "dc_description", "dc_subject", "dc_title", "dc_type",
	// "description", "europeana_collectionName",
	// "europeana_completeness", "europeana_country",
	// "europeana_dataProvider", "europeana_isShownAt",
	// "europeana_isShownBy", "europeana_language", "europeana_object",
	// "europeana_provider", "europeana_type", "europeana_uri", "indent",
	// "params", "q", "response", "responseHeader", "rows", "spell",
	// "start", "status", "subject", "timestamp", "title", "version",
	// "what", "when" };
	private Set<String> fields;
	Map<String, Long> fieldLengths;
	private IndexReader reader;

	public AverageFieldLengths(String solrIndexPath) {
		FSDirectory directory;
		try {
			directory = NIOFSDirectory.getDirectory(solrIndexPath);
			reader = IndexReader.open(directory);
		} catch (IOException e) {
			logger.error("reading the index ("+e.toString()+")");
			System.exit(-1);
		}
		loadFields();

	}

	public AverageFieldLengths(IndexReader reader) {
		this.reader = reader;
		loadFields();
	}

	private void loadFields() {
		fields = Collections.emptySet();
		if (reader.numDocs() == 0) {
			return;
		}
		try {

			fields = new HashSet<String>(fields.size());

			for (int i = 0; i < Math.min(reader.numDocs(), 1000); i++) {
				Document doc = reader.document(0);
				List<Fieldable> docFields = doc.getFields();
				for (Fieldable f : docFields) {
					fields.add(f.name());
				}
			}
		} catch (CorruptIndexException e) {
			logger.error("index is corrupted");
			return;

		} catch (IOException e) {
			logger.error("reading the index");
			return;
		}

	}

	/**
	 * Iterates over the first docs of the collection and compute the average
	 * length for each fields
	 * 
	 * @param numberOfDocs number of document to consider to computer the average length,
	 * if numberOfDocs <= 0, it will use the whole collection
	 * 
	 * @throws IOException
	 */
	public void load(int numberOfDocs) throws IOException {
		fieldLengths = new TreeMap<String, Long>();
		Map<String, byte[]> norms = new HashMap<String, byte[]>();
		int count = 0;

		for (String f : fields) {
			fieldLengths.put(f, 0L);
			norms.put(f, reader.norms(f));
		}

		if (numberOfDocs <= 0)
			numberOfDocs = reader.numDocs();
		// byte[] norm = reader.norms(this.fields[i]);
		for (int i = 0; i < numberOfDocs; i++) {

			try {
				Document doc = reader.document(i);
			} catch (CorruptIndexException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for (String f : fields) {
				byte[] norm = norms.get(f);
				byte byteLength = norm[i];

				float decodedLength = Similarity.decodeNorm(byteLength);
				long length = (long) (1 / (decodedLength * decodedLength));
				fieldLengths.put(f, fieldLengths.get(f) + length);
			}
			count++;
			if (count % 100000 == 0) {
				logger.info(count
						+ " documents scanned for retrieving the avg length of the fields");
			}
		}
		for (String f : fields) {
			fieldLengths.put(f, fieldLengths.get(f) / count);
		}
	}

	/**
	 * Iterates over the collection and compute the average length for each
	 * fields
	 * 
	 * @throws IOException
	 */
	public void load() throws IOException {
		load(-1);

	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, Long> entry : fieldLengths.entrySet()) {
			sb.append(entry.getKey()).append("\t").append(entry.getValue());
			sb.append("\n");
		}
		return sb.toString();
	}

}
