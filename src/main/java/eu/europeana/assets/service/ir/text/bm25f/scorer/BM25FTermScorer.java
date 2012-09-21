/*
 *  Copyright 2008 Joaquin Perez-Iglesias
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

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.search.TermQuery;

import eu.europeana.assets.service.ir.text.bm25f.BMParameters;


/**
 * Calculate the relevance value of a term applying BM25F ranking function.
 * 
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * @since 13/dec/2010
 */

public class BM25FTermScorer extends Scorer {
	private TermDocs[] termDocs;
	private TermQuery term;
	private float idf = 0f;
	private IndexReader reader;
	private String[] fields;
	private float[] boost;
	private float[] bParam;
	private BMParameters bmParams;
	private int doc = -1;
	private boolean initializated = false;
	private Similarity similarity;

	public BM25FTermScorer(IndexReader reader, TermQuery term,
			Similarity similarity, BMParameters bmParams) {
		super(similarity);
		this.similarity = similarity;
		this.reader = reader;
		this.term = term;
		this.bmParams = bmParams;
		this.fields = bmParams.getFields();
		this.boost = bmParams.getBoosts();
		this.bParam = bmParams.getbParams();
		this.termDocs = new TermDocs[this.fields.length];

		try {
			for (int i = 0; i < this.fields.length; i++)
				this.termDocs[i] = this.reader.termDocs(new Term(
						this.fields[i], term.getTerm().text()));

			this.idf = this.getSimilarity().idf(
					this.reader.docFreq(new Term(bmParams.getMainField(), term
							.getTerm().text())), this.reader.numDocs());

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.lucene.search.Scorer#docID()
	 */
	@Override
	public int docID() {
		return this.doc;
	}

	/* FIXME speed up, break the cicle if you have found it. */
	private boolean init() throws IOException {
		boolean result = false;
		int min = NO_MORE_DOCS;
		for (int i = 0; i < this.fields.length; i++) {
			if (this.termDocs[i].next()) {
				result = true;
				min = Math.min(min, this.termDocs[i].doc());
			}
		}
		doc = min;
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.lucene.search.Scorer#explain(int)
	 * 
	 * 
	 * private boolean init() throws IOException { boolean result = false; for
	 * (int i = 0; i < this.fields.length; i++) { this.termDocsNext[i] =
	 * this.termDocs[i].next(); if (this.termDocsNext[i] &&
	 * this.termDocs[i].doc() < this.doc) { result = true; this.doc =
	 * this.termDocs[i].doc(); } } return result; }
	 * 
	 * 
	 * /* (non-Javadoc)
	 * 
	 * @see org.apache.lucene.search.Scorer#score()
	 */
	@Override
	public float score() throws IOException {
		float acum = 0f;

		for (int i = 0; i < this.fields.length; i++) {

			if (this.termDocs[i].doc() == doc) {
				byte[] norm = this.reader.norms(this.fields[i]);

				float av_length = (float) bmParams
						.getAverageLength(this.fields[i]);
				float length = 0f;
				float normV = Similarity.decodeNorm(norm[this.docID()]);
				length = 1 / (normV * normV);

				float aux = 0f;
				aux = this.bParam[i] * length / av_length;
				aux = aux + 1 - this.bParam[i];
				acum += (this.term.getBoost() * this.boost[i] * this.termDocs[i]
						.freq()) / aux;
			}
		}

		acum = acum / (bmParams.getK1() + acum);
		acum = acum * this.idf;

		return acum;
	}

	@Override
	/*
	 * Advances to the next document in the set and returns the doc it is
	 * currently on, or #NO_MORE_DOCS if there are no more docs in the set.
	 */
	public int nextDoc() throws IOException {
		if (!initializated) {
			this.initializated = true;
			if (this.init()) {
				return this.doc;
			} else {
				return NO_MORE_DOCS;
			}
		}

		int min = NO_MORE_DOCS;
		for (int i = 0; i < this.fields.length; i++) {
			boolean hasNext = false;
			if (this.termDocs[i].doc() == doc) {
				hasNext = this.termDocs[i].next();
				if (hasNext) {
					min = Math.min(min, this.termDocs[i].doc());
				}
			}

		}
		doc = min;
		return doc;

	}

	public int advance(int target) throws IOException {
		while ((doc = nextDoc()) < target) {
		}
		return doc;
	}

	public boolean next() throws IOException {

		if (!initializated) {
			this.initializated = true;
			return this.init();
		}

		nextDoc();
		return this.doc != NO_MORE_DOCS;

	}

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
		BM25FTermScorer s = new BM25FTermScorer(reader,term, this.similarity,this.bmParams);
		if (s.advance(doc) == NO_MORE_DOCS)
			return null;
		float acum = 0f;
		Explanation result = new Explanation();
		Explanation tf = new Explanation();
		result.setDescription("BM25F (" + s.term.getTerm().text() + ")");

		for (int i = 0; i < s.fields.length; i++) {

			if (s.termDocs[i].doc() == doc) {
				Explanation partial = new Explanation();

				byte[] norm = s.reader.norms(s.fields[i]);

				float av_length = (float) bmParams
						.getAverageLength(s.fields[i]);
				float length = 1 / ((Similarity.decodeNorm(norm[s.doc()])) * (Similarity
						.decodeNorm(norm[s.doc()])));

				float aux = 0f;
				aux = s.bParam[i] * length / av_length;

				aux = aux + 1 - s.bParam[i];
				acum += s.boost[i] * s.termDocs[i].freq() / aux;

				partial = new Explanation(s.boost[i]
						* s.termDocs[i].freq() / aux, "(" + s.fields[i]
						+ ":" + s.term.getTerm().text() + ") B:"
						+ s.bParam[i] + ",Length:" + length + ",AvgLength:"
						+ av_length + ",Freq:" + s.termDocs[i].freq()
						+ ",Boost:" + s.boost[i]);
				Explanation idf = new Explanation(s.idf, "*IDF*");
				tf.addDetail(idf);
				tf.addDetail(partial);
			}
		}

		Explanation idfE = new Explanation(s.idf, " idf (docFreq:"
				+ s.reader.docFreq(new Term(bmParams.getMainField(),
						s.term.getTerm().text())) + ",numDocs:"
				+ s.reader.numDocs() + ")");
		result.addDetail(idfE);

		tf.setDescription("K1: " + acum + "/(" + acum + " + "
				+ bmParams.getK1() + ")");

		acum = acum / (bmParams.getK1() + acum);
		tf.setValue(acum);
		result.addDetail(tf);
		acum = acum * s.idf;
		result.setValue(acum);

		return result;
	}

}