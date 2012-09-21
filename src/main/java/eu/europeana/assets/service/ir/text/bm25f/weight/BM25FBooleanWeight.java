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
package eu.europeana.assets.service.ir.text.bm25f.weight;

import java.io.IOException;
import java.util.Set;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Weight;

import eu.europeana.assets.service.ir.text.bm25f.BM25FParameters;
import eu.europeana.assets.service.ir.text.bm25f.BMParameters;
import eu.europeana.assets.service.ir.text.bm25f.scorer.BM25FBooleanScorer;
import eu.europeana.assets.service.ir.text.bm25f.similarity.BM25Similarity;


/**
 * Weight BM25F class, implements <I>public Scorer scorer(IndexReader reader)
 * could throw IOException</I> <BR>
 * and <I>public Explanation explain(IndexReader reader, int doc) throws
 * IOException </I><BR>
 * 
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * @since 13/dec/2010
 */
public class BM25FBooleanWeight extends Weight {

	private static final long serialVersionUID = 1L;
	private BM25FParameters bmParams;
	private Set<Term> terms;

	public BM25FBooleanWeight(Set<Term> terms, BM25FParameters bmParams) {
		this.terms = terms;
		this.bmParams = bmParams;
	}

	/**
	 * Return null
	 * 
	 * @see org.apache.lucene.search.Weight#explain(org.apache.lucene.index.IndexReader,
	 *      int)
	 */
	@Override
	public org.apache.lucene.search.Explanation explain(IndexReader reader,
			int doc) throws IOException {
//		if (this.fields == null)
//			return new BM25FBooleanScorer(reader, terms, new BM25Similarity())
//					.explain(doc);
//		else
			return new BM25FBooleanScorer(reader, terms, new BM25Similarity(),
					bmParams).explain(doc);
	}

	/*
	 * Return null
	 * 
	 * @see org.apache.lucene.search.Weight#getQuery()
	 */
	@Override
	public Query getQuery() {
		return null;
	}

	/**
	 * Return 0
	 * 
	 * @see org.apache.lucene.search.Weight#getValue()
	 */
	@Override
	public float getValue() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.lucene.search.Weight#normalize(float)
	 */
	@Override
	public void normalize(float norm) {

	}


	@Override
	public Scorer scorer(IndexReader reader, boolean scoreDocsInOrder,
			boolean topScorer) throws IOException {
			return new BM25FBooleanScorer(reader, terms, new BM25Similarity(),
					bmParams);

	}

	/**
	 * Return 0.
	 * 
	 * @see org.apache.lucene.search.Weight#sumOfSquaredWeights()
	 */
	@Override
	public float sumOfSquaredWeights() throws IOException {
		return 0;
	}

}
