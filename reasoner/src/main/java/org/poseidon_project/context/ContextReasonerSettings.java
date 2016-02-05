/*
 * Copyright 2016 POSEIDON Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.poseidon_project.context;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * The user visible activity to personalise context rules
 *
 * @author Dean Kramer <d.kramer@mdx.ac.uk>
 */
public class ContextReasonerSettings extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new ContextReasonerSettingsFragment())
                .commit();

    }

    public static class ContextReasonerSettingsFragment extends PreferenceFragment {

    }
}