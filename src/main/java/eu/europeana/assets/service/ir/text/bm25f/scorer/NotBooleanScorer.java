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
 * Boolean Scorer matching all documents that do NOT contain any term (NOT
 * operator)
 * 
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * @since 13/dec/2010
 */
public class NotBooleanScorer extends AbstractBooleanScorer {

	private int doc = -1;
	private int numDocs;

	public NotBooleanScorer(Similarity similarity, Scorer[] scorer, int numDocs)
			throws IOException {
		super(similarity, scorer);
		this.numDocs = numDocs;
		for (int i = 0; i < this.subScorer.length; i++)
			this.subScorerNext[i] = this.subScorer[i].next();

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

	/**
	 * <B>Return null</B><BR>
	 * 
	 * @see org.apache.lucene.search.Scorer#explain(int)
	 */
	@Override
	public Explanation explain(int doc) throws IOException {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.lucene.search.Scorer#next()
	 */
	@Override
	public boolean next() throws IOException {
		while (this.doc < this.numDocs - 1) {
			this.doc++;
			int count = 0;
			for (int i = 0; i < this.subScorer.length; i++) {
				if (this.subScorerNext[i])
					if (this.subScorer[i].doc() != this.doc) {
						count++;
					} else {
						this.subScorerNext[i] = this.subScorer[i].next();
						count = 0;
					}
				else
					count++;
				if (count == this.subScorer.length)
					return true;
			}
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.lucene.search.Scorer#score()
	 */
	@Override
	public float score() throws IOException {
		return 1;
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

}
