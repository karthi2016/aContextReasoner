/*Copyright 2014 POSEIDON Project

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.poseidon_project.contexts.hardware;

import org.poseidon_project.contexts.ContextReceiver;
import org.poseidon_project.contexts.SensorContext;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

/**
 * @author Dean Kramer <d.kramer@mdx.ac.uk>
 *
 */
public class CompassContext extends SensorContext {


	private long mCurrentDegrees;

	public CompassContext(Context c, ContextReceiver cr) {
		//super(c, cr, Sensor.TYPE_MAGNETIC_FIELD, SensorManager.SENSOR_DELAY_NORMAL, "CompassContext");
		super(c, cr, Sensor.TYPE_ORIENTATION, SensorManager.SENSOR_DELAY_NORMAL, "CompassContext");
	}


	@Override
	protected void checkContext(float[] values) {
		long degree = Math.round(values[0]);
		if (degree != mCurrentDegrees) {
			mCurrentDegrees = degree;
			mReceiver.newContextValue("sensor.compass_degrees", mCurrentDegrees);
		}

	}

}
