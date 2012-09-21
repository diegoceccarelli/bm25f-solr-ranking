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
package eu.europeana.assets.service.ir.text.bm25f.scorer;

import java.io.IOException;
import java.util.Set;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.search.TermQuery;

import eu.europeana.assets.service.ir.text.bm25f.BM25FParameters;
import eu.europeana.assets.service.ir.text.bm25f.BMParameters;


/**
 * BM25FBooleanScorer, evaluates the total relevance value based on a boolean
 * expression
 * 
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * @since 16/dec/2010
 */
public class BM25FBooleanScorer extends Scorer {

	private int doc = -1;
	private boolean initialized = false;
	private BM25FParameters bmParams;
	private Scorer[] termScorers;
	private IndexReader reader; 
	private Similarity similarity;
	private Set<Term> terms;
	

	// public BM25FBooleanScorer(IndexReader reader, Set<Term> terms,
	// Similarity similarity) throws IOException {
	// super(similarity);
	// termScorers = new Scorer[terms.size()];
	// Scorer[] termScorers = new Scorer[terms.size()];
	// int i = 0;
	// for (Term t : terms) {//FIXME bm25params?
	// termScorers[i++] = new BM25TermScorer(reader, new TermQuery(t),
	// similarity, null);
	//
	// }
	//
	// }

	public BM25FBooleanScorer(IndexReader reader, Set<Term> terms,
			Similarity similarity, BM25FParameters bmParams) throws IOException {
		super(similarity);
		this.bmParams = bmParams;
		this.similarity = similarity;
		this.reader = reader; 
		this.terms = terms;
		termScorers = new Scorer[terms.size()];
		int i = 0;
		for (Term t : terms) {
			termScorers[i++] = new BM25FTermScorer(reader, new TermQuery(t),
					similarity, this.bmParams);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.lucene.search.Scorer#doc()
	 */
	@Override
	public int doc() {
		return this.doc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.lucene.search.Scorer#explain(int)
	 */
	@Override
	public Explanation explain(int doc) throws IOException {
		BM25FBooleanScorer s = new BM25FBooleanScorer(reader, terms, similarity, bmParams);

		if (s.advance(doc) != doc)
			return null;
		Explanation result = new Explanation();
		result.setDescription("Total");
		float value = 0f;
		for (int i = 0; i < s.termScorers.length; i++) {
			if (s.termScorers[i].docID() == doc) {
				value += s.termScorers[i].score();
				result.addDetail(s.termScorers[i].explain(doc));
			}
		}
		result.setValue(value);
		return result;
	}

	private void init() throws IOException {
		for (int i = 0; i < termScorers.length; i++) {
			termScorers[i].nextDoc();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.lucene.search.Scorer#next()
	 */
	@Override
	public boolean next() throws IOException {
		
		int minOcc = termScorers.length;
		if (bmParams.isInOR()){
			minOcc = 1;
		} else {
			if (bmParams.getMinNumberOfTerms() > 0){
				minOcc = Math.min(minOcc, bmParams.getMinNumberOfTerms());
			}
		}

		if (!this.initialized) {
			this.initialized = true;
			this.init();
		}

		int count = 0;
		int min = NO_MORE_DOCS;
		while (count < minOcc) {
			min = NO_MORE_DOCS;
			count = 0;
			for (int i = 0; i < termScorers.length; i++) {
				if (termScorers[i].docID() == doc)
					termScorers[i].nextDoc();
				if (termScorers[i].docID() == min)
					count++;
				if (termScorers[i].docID() < min) {
					min = termScorers[i].docID();
					count = 1;
				}
			}
			// at the end of the for:
			// min > docId()
			doc = min; // --> doc = min doc

		}
		return doc != NO_MORE_DOCS;

	}
	
	@Override
	public int nextDoc() throws IOException{
		int minOcc = termScorers.length;

		if (!this.initialized) {
			this.initialized = true;
			this.init();
		}
	
		if (bmParams.isInOR()){
			minOcc = 1;
		} else {
			if (bmParams.getMinNumberOfTerms() > 0){
				minOcc = Math.min(minOcc, bmParams.getMinNumberOfTerms());
	
			}
		}


		int count = 0;
		
		
		int min = NO_MORE_DOCS;
		while (count < minOcc) {
			min = NO_MORE_DOCS;
			//count = 0;
			for (int i = 0; i < termScorers.length; i++) {
				if (termScorers[i].docID() == doc)
					termScorers[i].nextDoc();
				if (termScorers[i].docID() == min)
					count++;
				if (termScorers[i].docID() < min) {
					min = termScorers[i].docID();
					count = 1;
				}
			}
			// at the end of the for:
			// min > docId()
			doc = min; // --> doc = min doc

		}
		return doc;
	}
	
	@Override
	public int advance(int target) throws IOException{
		if (target == NO_MORE_DOCS ) return NO_MORE_DOCS;
		while ((doc = nextDoc()) < target) {
		}
		return doc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.lucene.search.Scorer#score()
	 */
	@Override
	public float score() throws IOException {
		float result = 0f;
		for (int i = 0; i < termScorers.length; i++) {
			if (termScorers[i].docID() == doc)
				result += termScorers[i].score();
		}

		return result;
	}

	

}
