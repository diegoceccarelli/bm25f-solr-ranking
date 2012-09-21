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
package eu.europeana.assets.service.ir.text.bm25f.query;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.Weight;

import eu.europeana.assets.service.ir.text.bm25f.BM25FParameters;
import eu.europeana.assets.service.ir.text.bm25f.weight.BM25FBooleanWeight;


/**
 * Models a BM25F Query (@see org.apache.lucene.search.Query)
 * 
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * @since 14/dec/2010
 */
public class BM25FBooleanQuery extends BMBooleanQuery {

	private static final long serialVersionUID = 1L;
	private BM25FParameters bmParams;

	/**
	 * Builds a query that will use BM25F ranking function on the collection
	 * 
	 * @param query
	 *            - the user query
	 * @param analyzer
	 *            - query analyzer (@see org.apache.lucene.analysis.Analyzer)
	 * @param bmParams
	 *            - the ranking parameters
	 */
	public BM25FBooleanQuery(String query, Analyzer analyzer,
			BM25FParameters bmParams) throws ParseException, IOException {
		super(query, analyzer, bmParams);
		this.bmParams = bmParams;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see bm25f.query.BMBooleanQuery#weight(org.apache.lucene.search.Searcher)
	 */
	@Override
	public Weight weight(Searcher searcher) throws IOException {
		return new BM25FBooleanWeight(getTerms(), bmParams);
	}

}