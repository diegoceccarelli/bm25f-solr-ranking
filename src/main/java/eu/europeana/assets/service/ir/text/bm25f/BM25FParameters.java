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
package eu.europeana.assets.service.ir.text.bm25f;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * Parameters needed to calculate the BM25F relevance score.
 * 
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * @since 13/dec/2010
 */

public class BM25FParameters extends BMParameters {
	
	private enum Operator {AND,OR};
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Arrays.hashCode(bParams);
		result = prime * result + Arrays.hashCode(boosts);
		result = prime * result
				+ ((idfField == null) ? 0 : idfField.hashCode());
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
		BM25FParameters other = (BM25FParameters) obj;
		if (!Arrays.equals(bParams, other.bParams))
			return false;
		if (!Arrays.equals(boosts, other.boosts))
			return false;
		if (idfField == null) {
			if (other.idfField != null)
				return false;
		} else if (!idfField.equals(other.idfField))
			return false;
		return true;
	}

	/* the main field in the collection, used to calculate the
	 * inverse document frequency
	 */
	private String idfField = "text";
	/* field names of a record ( title, author, and so on) */
	private String[] fields;
	/* boosts on fields, you can boost more the match on a field 
	 * rather than another
	 */
	private float[] boosts;
	/* boosts on length, you can boost a record if a 
	 * field length is similar to the average field
	 * length in the collection
	 */
	private float[] bParams;
	
	/* how to perform the query, AND retrieve documents
	 * that contains all the term (or at least minNumberOfTerms, i
	 * if it is set) OR retrieves documents that contains AT LEAST
	 * one term of the query.
	 */
	private Operator operator = Operator.AND;
	
	private int minNumberOfTerms = -1;
	
	

	public BM25FParameters() {
	};

	/**
	 * Returns the name of the field on which to compute the inverse document
	 * frequency. If the user did not specify the value of this field in the
	 * config file, it returns the name of the field with the longest average
	 * length.
	 * 
	 * @return The field on which compute the inverse document frequency.
	 */
	public String getIdfField() {
		if (idfField == null) {
			float max = -1;
			String maxField = "";
			Iterator<Entry<String, Float>> iter = this.avgLength
					.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<String, Float> entry = iter.next();
				if (entry.getValue() > max) {
					max = entry.getValue();
					maxField = entry.getKey();
				}

			}
			idfField = maxField;
		}
		return idfField;

	}
	
	public void performQueriesInOR(){
		operator = Operator.OR;
	}
	
	public boolean isInOR(){
		return operator == Operator.OR;
	}
	
	
	/**
	 * Sets on which compute the inverse document frequency
	 * @param idfField - The field on which compute the inverse document frequency.
	 */
	public void setIdfField(String idfField) {
		this.idfField = idfField;
	}

	/**
	 * @param fields - the fields to set
	 */
	public void setFields(String[] fields) {
		this.fields = fields;
	}

	/**
	 * @return the fields
	 */
	public String[] getFields() {
		return fields;
	}

	/**
	 * @param boosts - the boosts to set (see bm25f formula)
	 */
	public void setBoosts(float[] boosts) {
		this.boosts = boosts;
	}

	/**
	 * @return the boosts (see bm25f formula)
	 */
	public float[] getBoosts() {
		return boosts;
	}

	/**
	 * @param bParams the bParams to set (see bm25f formula)
	 */
	public void setbParams(float[] bParams) {
		this.bParams = bParams;
	}

	/**
	 * @return the bParams (see bm25f formula)
	 */
	public float[] getbParams() {
		return bParams;
	}

	public int getMinNumberOfTerms() {
		return minNumberOfTerms;
	}

	public void setMinNumberOfTerms(int minNumberOfTerms) {
		this.minNumberOfTerms = minNumberOfTerms;
	}

}
