package test;

import java.util.Collections;
import java.util.List;
import java.lang.Math;

/*
*********************************************************************************

 extract_features

*********************************************************************************
 Description:

 Extracts statistical features from a data set (python list).

*********************************************************************************
 Author: Rob Lyon
 Email : robert.lyon@manchester.ac.uk
 web   : www.scienceguyrob.com
*********************************************************************************
 License:

 Code made available under the GPLv3 (GNU General Public License), that
 allows you to copy, modify and redistribute the code as you see fit
 (http://www.gnu.org/copyleft/gpl.html). Though a mention to the
 original author using the citation above in derivative works, would be
 very much appreciated.
*********************************************************************************
*/

public class FeaturesExtraction {

	/*
	 * Extracts statistics from the values stored in the supplied data array.
	 * 
	 * Parameters ---------- :param data: a python list containing numerical
	 * entries. This list contains the mean, standard deviation, skew and kurtosis.
	 * 
	 * Returns ---------- :return: a list if the statistics were computed
	 * successfully, else None.
	 */

	public List<Float> extract_features(List<Float> data) {

		if (data != null) { // Check data is not empty
			if (data.size() > 0) {

				float min_value = Float.MAX_VALUE;
				float max_value = Float.MIN_VALUE;

				// First ensure the data is sorted from smallest to largest value.
				Collections.sort(data);

				// Sums computed during calculation.
				float mean_sum = (float) 0.0;
				float mean_subtracted_sum_power_2 = (float) 0.0;
				float mean_subtracted_sum_power_3 = (float) 0.0;
				float mean_subtracted_sum_power_4 = (float) 0.0;

				// The number of data points in the array.
				int n = data.size();

				// Necessary first loop to calculate the sum, min and max
				for (float d : data) {
					mean_sum += d;

					if (d < min_value)
						min_value = d;

					if (d > max_value)
						max_value = d;
				}
				float q1, q3 = 0;
				// Compute median, q1, q3, and IQR.
				if (n % 2 == 0) { // Length of data is even
					// OK, an even length means there is no middle element. So first
					// we compute the median.... e.g. suppose we have this data
					//
					// middle
					// |
					// V
					// 0 1 2 3 4 5 6 7 8 9 <- Index, 10 elements.
					// data = [ 1 , 2 , 3 , 4 , 5 , 5 , 4 , 3 , 2 , 1 ]
					int middle = (n / 2); // Midpoint
					float median = (data.get(middle - 1) + data.get(middle)) / 2;
					q1 = data.get((int) (middle / 2));
					q3 = data.get((int) (middle / 2) + (int) (middle));
				} else { // Length of data is odd
					// OK, an odd length means there is a middle element. So first
					// we compute the median.... e.g. suppose we have this data
					//
					// middle
					// |
					// V
					// 0 1 2 3 4 5 6 7 8 <- Index, 9 elements.
					// data = [ 1 , 2 , 3 , 4 , 5 , 4 , 3 , 2 , 1 ]
					int middle = (int) (n / 2); // Midpoint
					float median = data.get(middle);
					// The bottom half, and top half of the data will be even, so Q1 and Q3
					// have to be computed from two elements.
					q1 = (data.get((int) (middle / 2) - 1) + data.get((int) (middle / 2))) / 2;
					q3 = (data.get(middle + ((int) (middle / 2))) + data.get(middle + ((int) (middle / 2) + 1))) / 2;
				}
				// Compute IQR
				float iqr = q3 - q1;

				// Update the range
				float range_value = max_value - min_value;

				if (mean_sum > 0) { // If the mean is greater than zero (should be)
					// Update the mean value.
					float mean_ = (float) (mean_sum) / (float) (n);

					// Now try to compute the standard deviation, using
					// the mean computed above... we also compute values in
					// this loop required to compute the excess Kurtosis and
					// standard deviation.

					for (float d : data) {

						mean_subtracted_sum_power_2 += Math.pow(d - mean_, 2);

						// Used to compute skew
						mean_subtracted_sum_power_3 += Math.pow(d - mean_, 3);

						// Used to compute Kurtosis
						mean_subtracted_sum_power_4 += Math.pow(d - mean_, 4);
					}
					// Update the standard deviation value.
					float stdev_ = (float) Math.sqrt(mean_subtracted_sum_power_2 / n);
					float var_ = stdev_ * stdev_;
					// Next try to calculate the excess Kurtosis and skew using the
					// information gathered above.

					float one_over_n = (float) (1.0 / n); // Used multiple times...

					float kurt_ = (float) ((one_over_n * mean_subtracted_sum_power_4)
							/ Math.pow(one_over_n * mean_subtracted_sum_power_2, 2) - 3);

					float skew_ = (float) ((one_over_n * mean_subtracted_sum_power_3)
							/ Math.pow(Math.sqrt(one_over_n * mean_subtracted_sum_power_2), 3));

					List<Float> statistics = null;
					statistics.add(mean_);
					statistics.add(stdev_);
					statistics.add(skew_);
					statistics.add(kurt_);
					return statistics;
				} else { // Data sums to zero, i.e. no data!
					return null;
				}
			} else { // Data empty for some reason...
				return null;
			}
		}
		return null;
	}
	
	
	public void featureExtractionTest() {
		//TODO
		//reszta kodu
	}

}
