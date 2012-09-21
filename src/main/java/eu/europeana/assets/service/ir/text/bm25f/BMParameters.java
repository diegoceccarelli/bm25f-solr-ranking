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
package eu.europeana.assets.service.ir.text.bm25f;

import java.util.HashMap;
import java.util.Map;

/**
 * Parameters needed to calculate the BM25/BM25F relevance score.
 * 
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * @since 14/dec/2010
 */
public abstract class BMParameters {
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((avgLength == null) ? 0 : avgLength.hashCode());
		result = prime * result + Float.floatToIntBits(k1);
		result = prime * result
				+ ((mainField == null) ? 0 : mainField.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BMParameters other = (BMParameters) obj;
		if (avgLength == null) {
			if (other.avgLength != null)
				return false;
		} else if (!avgLength.equals(other.avgLength))
			return false;
		if (Float.floatToIntBits(k1) != Float.floatToIntBits(other.k1))
			return false;
		if (mainField == null) {
			if (other.mainField != null)
				return false;
		} else if (!mainField.equals(other.mainField))
			return false;
		return true;
	}

	protected  Map<String, Float> avgLength = new HashMap<String, Float>();
	private float k1 = 2f;
	private String mainField; 
	/**
	 * 
	 * @return the parameter k1, by default is 2 (see bm25 formula)
	 */
	public float getK1() {
		return k1;
	}

	/**
	 * Sets the k1 parameter
	 * 
	 * @param k1 - the saturation parameter
	 */
	public void setK1(float k1) {
		this.k1 = k1;
	}
	
	/**
	 * Sets the average length (in terms) for the field 'field'
	 * 
	 * @param field  
	 * @param avg - average length (in terms) of the field
	 */
	public  void setAverageLength(String field, float avg) {
		this.avgLength.put(field, avg);
	}

	/**
	 * Return the field 'field' average length (in terms)
	 * 
	 * @param field
	 * @return average length of the field
	 */
	public  float getAverageLength(String field) {
		try {
			return this.avgLength.get(field);
		} catch (NullPointerException e) {
			System.out
					.println("Can't find average length for field '"
							+ field
							+ "' you have to set field average length values, before execute a search.");
			throw e;
		}
	}

	/**
	 * @param mainField the mainField to set
	 */
	public void setMainField(String mainField) {
		this.mainField = mainField;
	}

	/**
	 * @return the mainField
	 */
	public String getMainField() {
		return mainField;
	}
	
	public abstract String[] getFields();

	/**
	 * @return
	 */
	public float[] getBoosts() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return
	 */
	public abstract float[] getbParams();

}
