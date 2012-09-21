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
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Similarity;

/**
 * An abstract class for BM25 Boolean Scorers.
 * 
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * @since 13/dec/2010
 */
public abstract class AbstractBooleanScorer extends Scorer {
	/*It contains an array of subScorers and another indicating which scorer has a next document */
	protected Scorer[] subScorer;
	protected boolean subScorerNext[];

	protected AbstractBooleanScorer(Similarity similarity, Scorer scorer[])
			throws IOException {
		super(similarity);
		this.subScorer = scorer;
		if (scorer != null && scorer.length > 0)
			this.subScorerNext = new boolean[this.subScorer.length];
	}
}
