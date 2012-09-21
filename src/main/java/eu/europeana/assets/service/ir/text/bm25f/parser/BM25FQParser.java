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

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.search.QParser;
import org.apache.solr.search.SolrQueryParser;
import org.apache.solr.search.LuceneQParserPlugin;


import com.google.gson.Gson;

import eu.europeana.assets.service.ir.text.bm25f.BM25FParameters;
import eu.europeana.assets.service.ir.text.bm25f.query.BM25FBooleanQuery;



/**
 * Parser for a BM25F query
 * 
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * @since 13/dec/2010
 */
public class BM25FQParser extends QParser {
	/* parameters for the bm25f ranking function*/
	public BM25FParameters bmParams;
	private static Logger log = Logger.getLogger(BM25FQParser.class);
	private static Gson gson = new Gson();

	public BM25FQParser(String qstr, SolrParams localParams, SolrParams params,
			SolrQueryRequest req, BM25FParameters bmParams) {
		super(qstr, localParams, params, req);
		this.bmParams = bmParams;

	}

	@SuppressWarnings("unchecked")
	@Override
	public Query parse() throws ParseException {

		/*
		 * SolrParams solrParams = localParams == null ? params : new
		 * DefaultSolrParams(localParams, params);
		 */

		/* Main User Query */
		String userQuery = getString();
		/* String parsedUserQuery = null; */
		
		/* 27/03/2012 current version of the plugin does not manage Boolean queries */
		if  (userQuery.contains(" AND ") || userQuery.contains(" OR ")){ 
			QParser p = new LuceneQParserPlugin().createParser(qstr,localParams, params, req);
			return p.parse();
		
		}
		
		String k1s = this.getParam("k1");
		if (k1s != null){
			float k1 = Float.parseFloat(k1s);
			bmParams.setK1(k1);
		}
		
		
		
		String boostss = this.getParam("boosts");
		if (boostss != null){
			float[] boosts = gson.fromJson(boostss, float[].class);
			bmParams.setBoosts(boosts);
		}
		
		String bParamss = this.getParam("b");
		if (bParamss != null){
			float[] bParams = gson.fromJson(bParamss, float[].class); 
			bmParams.setbParams(bParams);
		}
		
		 

		SolrQueryParser sqp = new SolrQueryParser(req.getSchema(), bmParams
				.getMainField());
	
		Query q = sqp.parse(userQuery);
		
		
		
		if (q instanceof BooleanQuery) {
			List<BooleanClause> clauses = ((BooleanQuery) q).clauses();
			if (clauses.isEmpty()) return q;
			for (BooleanClause c : clauses) {
				Set<Term> terms = new HashSet<Term>();
				c.getQuery().extractTerms(terms);
				for (Term t : terms) {
					if (!t.field().equals(bmParams.getMainField())) {
						/* TODO manage different fields with bm25f */
						/*
						 * if the query is on fields different from the main, we
						 * process it as a standard solr query
						 */
						return q;
					}
				}
			}
			/*
			 * if I'm here, the query is a BooleanQuery on the default field, so
			 * I can use bm25f
			 */
			
			
			
			
			
			
			BM25FBooleanQuery bm25fQuery;
			try {
				bm25fQuery = new BM25FBooleanQuery(userQuery, req.getSchema()
						.getQueryAnalyzer(), bmParams);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				log.info("Error during the parsing of the BM25F-query "
						+ userQuery);
				System.err.println("Error during the parsing of the query "
						+ userQuery);
				/* we manage the error returning a standard solr query */
				return q;
			}
			return bm25fQuery;
		}
		if (q instanceof TermQuery) {
			TermQuery tq = (TermQuery) q;
			
			if (tq.getTerm().field().equals(bmParams.getMainField())) {
				try {
					return new BM25FBooleanQuery(tq.getTerm().text(), req
							.getSchema().getQueryAnalyzer(), bmParams);
				} catch (IOException e) {
					log.info("Error during the parsing of the BM25F-query "
							+ userQuery);
					log.error("Error during the parsing of the query "
							+ userQuery);
					return tq;
				}
			}
			return tq;
		}

		return q;

	}

	@Override
	public String[] getDefaultHighlightFields() {
		return bmParams.getFields();
	}

	@Override
	public Query getHighlightQuery() throws ParseException {
		return query;
	}

}
