/* Copyright 2016 Samsung Electronics Co., LTD
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

package com.samsungxr.debug;

import java.io.StringWriter;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import com.samsungxr.SXRContext;
import com.samsungxr.debug.cli.LineProcessor;

/*package*/ class ScriptHandler implements LineProcessor {
    protected String prompt;
    protected ScriptEngine mScriptEngine;
    protected ScriptContext mScriptContext;
    protected StringWriter mWriter;

    public ScriptHandler(SXRContext gvrContext, String prompt, ScriptEngine engine) {
        this.prompt = prompt;
        mScriptEngine = engine;
        mScriptContext = mScriptEngine.getContext();

        mWriter = new StringWriter();
        mScriptContext.setWriter(mWriter);
        mScriptContext.setErrorWriter(mWriter);
    }

    @Override
    public String processLine(String line) {
        try {
            mWriter.getBuffer().setLength(0);
            mScriptEngine.eval(line, mScriptContext);
            mWriter.flush();
            if (mWriter.getBuffer().length() != 0)
                return mWriter.toString();
            else
                return "";
        } catch (ScriptException e) {
            return e.toString();
        }
    }

    @Override
    public String getPrompt() {
        return prompt;
    }
}
