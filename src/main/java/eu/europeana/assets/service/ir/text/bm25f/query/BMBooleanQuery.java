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
package eu.europeana.assets.service.ir.text.bm25f.query;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.Weight;
import org.apache.lucene.util.Version;

import eu.europeana.assets.service.ir.text.bm25f.BMParameters;


/**
 * Models a BM25/BM25F Query (@see org.apache.lucene.search.Query)
 * 
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * @since 14/dec/2011
 */

public abstract class BMBooleanQuery extends BooleanQuery {
	private static final long serialVersionUID = 1L;
	private int doc = -1;
	private Set<Term> terms = new HashSet<Term>();
	private BMParameters bmParams;

	/**
	 * Builds a query that will use BM25/BM25F ranking function
	 * 
	 */
	public BMBooleanQuery(String query, Analyzer analyzer, BMParameters bmParams)
			throws ParseException, IOException {
		QueryParser qp = new QueryParser(Version.LUCENE_29,
				bmParams.getMainField(), analyzer);
		Query q = qp.parse(query);
		this.bmParams = bmParams;

		if (q instanceof BooleanQuery) {
			BooleanQuery bq = (BooleanQuery) q;
			List<BooleanClause> clauses = bq.clauses();
			for (BooleanClause cl : clauses) {
				cl.getQuery().extractTerms(terms);
			}
		} else {
			q.extractTerms(terms);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((bmParams == null) ? 0 : bmParams.hashCode());
		result = prime * result + ((terms == null) ? 0 : terms.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		BMBooleanQuery other = (BMBooleanQuery) obj;
		if (bmParams == null) {
			if (other.bmParams != null)
				return false;
		} else if (!bmParams.equals(other.bmParams))
			return false;
		if (terms == null) {
			if (other.terms != null)
				return false;
		} else if (!terms.equals(other.terms))
			return false;
		return true;
	}

	public int doc() {
		return this.doc;
	}
	
	

	public Set<Term> getTerms() {
		return terms;
	}

	public void setTerms(Set<Term> terms) {
		this.terms = terms;
	}

	@SuppressWarnings("unchecked")
	public void extractTerms(Set queryterms) {
		queryterms.addAll(terms);
	}

	public abstract Weight weight(Searcher searcher) throws IOException;

	

}
