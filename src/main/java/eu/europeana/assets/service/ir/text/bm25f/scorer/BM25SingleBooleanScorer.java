///*
// *  Copyright 2008 Joaquin Perez-Iglesias
// *  Copyright 2010 Diego Ceccarelli
// *
// *  Licensed under the Apache License, Version 2.0 (the "License");
// *  you may not use this file except in compliance with the License.
// *  You may obtain a copy of the License at
// * 
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// *  Unless required by applicable law or agreed to in writing, software
// *  distributed under the License is distributed on an "AS IS" BASIS,
// *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *  See the License for the specific language governing permissions and
// *  limitations under the License.
// *
// */
//package eu.europeana.assets.service.ir.text.bm25f.scorer;
//
//import java.io.IOException;
//
//import org.apache.lucene.index.IndexReader;
//import org.apache.lucene.search.BooleanClause;
//import org.apache.lucene.search.Explanation;
//import org.apache.lucene.search.Scorer;
//import org.apache.lucene.search.Similarity;
//
//import eu.europeana.assets.service.ir.text.bm25f.BM25FParameters;
//import eu.europeana.assets.service.ir.text.bm25f.BMParameters;
//
//
///**
// * BM25SingleBooleanScorer calculates the total relevance value based boolean
// * expression, that has just one common operator (AND, OR, NOT) for all terms.
// * 
// * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
// * @since 13/dec/2010
// */
//public class BM25SingleBooleanScorer extends Scorer {
//
//	private AbstractBooleanScorer booleanScorer = null;
//
//	// private BMParameters bmParams;
//
//	public BM25SingleBooleanScorer(IndexReader reader,
//			BooleanTermQuery[] termQuery, Similarity similarity,
//			BMParameters bmParams) throws IOException {
//		super(similarity);
//		// this.bmParams = bmParams;
//		Scorer[] scorer = new Scorer[termQuery.length];
//
//		for (int i = 0; i < scorer.length; i++) {
//			if(bmParams instanceof BM25FParameters){
//				scorer[i] = new BM25FTermScorer(reader, termQuery[i].termQuery,
//					similarity, bmParams);
//			}
//			
//		}
//
//		if (termQuery[0].occur == BooleanClause.Occur.MUST)
//			this.booleanScorer = new MustBooleanScorer(similarity, scorer);
//		else if (termQuery[0].occur == BooleanClause.Occur.SHOULD)
//			this.booleanScorer = new ShouldBooleanScorer(similarity, scorer);
//		else
//			this.booleanScorer = new NotBooleanScorer(similarity, scorer,
//					reader.numDocs());
//
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see org.apache.lucene.search.Scorer#doc()
//	 */
//	@Override
//	public int doc() {
//		return booleanScorer.doc();
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see org.apache.lucene.search.Scorer#explain(int)
//	 */
//	@Override
//	public Explanation explain(int doc) throws IOException {
//		Explanation result = new Explanation();
//		result.setDescription("Total");
//		Explanation detail = this.booleanScorer.explain(doc);
//		result.addDetail(detail);
//		result.setValue(detail.getValue());
//		return result;
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see org.apache.lucene.search.Scorer#next()
//	 */
//	@Override
//	public boolean next() throws IOException {
//		return booleanScorer.next();
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see org.apache.lucene.search.Scorer#score()
//	 */
//	@Override
//	public float score() throws IOException {
//		return booleanScorer.score();
//
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see org.apache.lucene.search.Scorer#skipTo(int)
//	 */
//	@Override
//	public boolean skipTo(int target) throws IOException {
//		while (this.next() && this.doc() < target) {
//		}
//
//		return this.doc() == target;
//	}
//
//}
