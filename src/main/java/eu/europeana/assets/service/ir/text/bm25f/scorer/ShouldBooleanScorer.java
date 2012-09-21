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
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Similarity;

/**
 * Boolean Scorer matching all documents containing at least one term (OR
 * operator)
 * 
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * @since 13/dec/2010
 */
public class ShouldBooleanScorer extends AbstractBooleanScorer {

	private boolean initializated = false;
	private int doc = Integer.MAX_VALUE;

	public ShouldBooleanScorer(Similarity similarity, Scorer scorer[])
			throws IOException {
		super(similarity, scorer);
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
		if (!this.skipTo(doc))
			return null;
		Explanation result = new Explanation();
		Explanation detail;
		result.setDescription("OR");
		float value = 0f;
		for (int i = 0; i < this.subScorer.length; i++) {
			if (this.subScorer[i].doc() == doc) {
				detail = this.subScorer[i].explain(doc);
				result.addDetail(detail);
				value += detail.getValue();
			}
		}
		result.setValue(value);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.lucene.search.Scorer#next()
	 */
	@Override
	public boolean next() throws IOException {
		if (!this.initializated) {
			this.initializated = true;
			return this.init();
		}
		int min = Integer.MAX_VALUE;
		/* AVANZO LOS TERMDOCS CON MENOR ID */
		for (int i = 0; i < this.subScorer.length; i++) {
			if (this.subScorerNext[i] && this.subScorer[i].doc() == this.doc) {
				this.subScorerNext[i] = this.subScorer[i].next();
			}
			if (this.subScorerNext[i] && this.subScorer[i].doc() < min)
				min = this.subScorer[i].doc();
		}
		return ((this.doc = min) != Integer.MAX_VALUE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.lucene.search.Scorer#skipTo(int)
	 */
	@Override
	public boolean skipTo(int target) throws IOException {
		while (this.doc() < target && this.next()) {
		}

		return this.doc() == target;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.lucene.search.Scorer#score()
	 */
	@Override
	public float score() throws IOException {
		double result = 0f;
		for (int i = 0; i < this.subScorer.length; i++) {
			if (this.subScorer[i].doc() == this.doc)
				result += this.subScorer[i].score();

		}
		return (float) result;
	}

	private boolean init() throws IOException {
		boolean result = false;
		for (int i = 0; i < this.subScorer.length; i++) {
			this.subScorerNext[i] = this.subScorer[i].next();
			if (this.subScorerNext[i] && this.subScorer[i].doc() < this.doc) {
				this.doc = this.subScorer[i].doc();
				result = true;
			}
		}
		return result;
	}

}
