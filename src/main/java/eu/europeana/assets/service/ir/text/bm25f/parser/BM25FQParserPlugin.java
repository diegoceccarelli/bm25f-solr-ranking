/*
 *  Copyright 2010 Diego Ceccarelli
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
package eu.europeana.assets.service.ir.text.bm25f.parser;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.QParser;
import org.apache.solr.search.QParserPlugin;

import eu.europeana.assets.service.ir.text.bm25f.BM25FParameters;

/**
 * This class takes the bm25f parameters from the Solr's config file and creates
 * an instance of a BM25FQParser
 * 
 * @see BM25FQParser
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * @since 07/aug/2010
 */

public class BM25FQParserPlugin extends QParserPlugin {

	private static Logger logger = Logger.getLogger(BM25FQParserPlugin.class);

	public static String NAME = "bm25f";
	/* saturation param (see bm25 formula) */
	private float k1 = 1.0f;

	private BM25FParameters bmParams;

	@Override
	public QParser createParser(String qstr, SolrParams localParams,
			SolrParams params, SolrQueryRequest req) {

		BM25FQParser parser = new BM25FQParser(qstr, localParams, params, req,
				bmParams);

		return parser;

	}

	@SuppressWarnings("unchecked")
	public void init(NamedList args) {
		bmParams = new BM25FParameters();
		if (args != null) {
			k1 = (Float) args.get("k1");
			String mainField = (String) args.get("mainField");

			String operator = (String) args.get("operator");
			if (operator != null) {
				if (operator.equals("OR")) {
					bmParams.performQueriesInOR();
				} else {
					Object tmp = args.get("minNumberOfTerms");
					if (tmp != null && tmp instanceof Integer) {
						Integer minNumberOfTerms = (Integer)tmp;
						if (minNumberOfTerms != null) {
							bmParams.setMinNumberOfTerms(minNumberOfTerms);
						}
					}
				}
			}

			Map<String, String> averageLengthFields = SolrParams
					.toMap((NamedList) args.get("averageLengthFields"));
			Map<String, String> fieldsBoost = SolrParams.toMap((NamedList) args
					.get("fieldsBoost"));
			Map<String, String> fieldsB = SolrParams.toMap((NamedList) args
					.get("fieldsB"));
			Object[] tmp = averageLengthFields.keySet().toArray();
			String[] fields = new String[tmp.length];
			for (int i = 0; i < tmp.length; i++) {
				fields[i] = (String) tmp[i];
			}
			Arrays.sort(fields);

			float[] boosts = new float[fields.length];
			float[] bParams = new float[fields.length];
			for (int i = 0; i < fields.length; i++) {
				boosts[i] = Float.parseFloat(fieldsBoost.get(fields[i]));
				bParams[i] = Float.parseFloat(fieldsB.get(fields[i]));
				float avg = Float
						.parseFloat(averageLengthFields.get(fields[i]));
				bmParams.setAverageLength(fields[i], avg);
				logger.info(fields[i] + "\tavgLen:" + avg + "\tboost:"
						+ boosts[i] + "\tbParam " + bParams[i]);
			}
			bmParams.setK1(k1);
			bmParams.setBoosts(boosts);
			bmParams.setFields(fields);
			bmParams.setbParams(bParams);
			bmParams.setMainField(mainField);
			bmParams.setIdfField(mainField);
		}
	}

}
