/**
 * Copyright (C) 2013 – 2015 SLUB Dresden & Avantgarde Labs GmbH (<code@dswarm.org>)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dswarm.converter.flow;


import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import com.google.common.base.Optional;
import com.google.common.io.CharStreams;
import org.culturegraph.mf.morph.Metamorph;
import org.culturegraph.mf.stream.pipe.Filter;

import org.dswarm.converter.DMPConverterException;
import org.dswarm.converter.DMPMorphDefException;
import org.dswarm.converter.morph.FilterMorphScriptBuilder;
import org.dswarm.converter.morph.MorphScriptBuilder;
import org.dswarm.persistence.model.job.Task;
import org.dswarm.persistence.model.resource.DataModel;

import static org.dswarm.converter.flow.TransformationFlow.createFilter;
import static org.dswarm.converter.flow.TransformationFlow.createMorph;
import static org.dswarm.converter.flow.TransformationFlow.readFile;
import static org.dswarm.converter.flow.TransformationFlow.readResource;
import static org.dswarm.converter.flow.TransformationFlow.readString;

public interface TransformationFlowFactory {
	TransformationFlow create(
			final Metamorph transformer,
			final String scriptArg,
			final Optional<DataModel> outputDataModelArg,
			final Optional<Filter> optionalSkipFilterArg);

	default TransformationFlow fromString(
			final String morphScriptString) throws DMPConverterException {

		return fromAnything(readString(morphScriptString));
	}

	default TransformationFlow fromString(
			final String morphScriptString,
			final String filterScriptString) throws DMPConverterException {

		return fromAnything(
				readString(morphScriptString),
				readString(filterScriptString));
	}

	default TransformationFlow fromString(
			final String morphScriptString,
			final DataModel outputDataModel) throws DMPConverterException {

		return fromAnything(
				readString(morphScriptString),
				outputDataModel);
	}

	default TransformationFlow fromString(
			final String morphScriptString,
			final String filterScriptString,
			final DataModel outputDataModel) throws DMPConverterException {

		return fromAnything(
				readString(morphScriptString),
				readString(filterScriptString),
				outputDataModel);
	}

	default TransformationFlow fromFile(final File morphFile) throws DMPConverterException {

		return fromAnything(readFile(morphFile));
	}

	default TransformationFlow fromFile(final File morphFile, final File filterFile) throws DMPConverterException {

		return fromAnything(
				readFile(morphFile),
				readFile(filterFile));
	}

	default TransformationFlow fromFile(final String morphResource) throws DMPConverterException {

		return fromAnything(readResource(morphResource));
	}

	default TransformationFlow fromFile(final String morphResource, final String filterResource) throws DMPConverterException {

		return fromAnything(
				readResource(morphResource),
				readResource(filterResource));
	}

	default TransformationFlow fromTask(final Task task) throws DMPConverterException {

		final String morphScriptString = new MorphScriptBuilder().apply(task).toString();
		final Optional<String> maybeFilterScript = Optional.fromNullable(
				new FilterMorphScriptBuilder().apply(task).toString());

		return fromAnything(
				readString(morphScriptString),
				readString(maybeFilterScript),
				Optional.fromNullable(task.getOutputDataModel()));
	}

	// private-ish

	default TransformationFlow fromAnything(
			final Reader morphScript) throws DMPMorphDefException {
		return fromAnything(morphScript, Optional.absent(), Optional.absent());
	}

	default TransformationFlow fromAnything(
			final Reader morphScript,
			final Reader filterScript) throws DMPMorphDefException {
		return fromAnything(morphScript, Optional.of(filterScript), Optional.absent());
	}

	default TransformationFlow fromAnything(
			final Reader morphScript,
			final DataModel dataModel) throws DMPMorphDefException {
		return fromAnything(morphScript, Optional.absent(), Optional.of(dataModel));
	}

	default TransformationFlow fromAnything(
			final Reader morphScript,
			final Reader filterScript,
			final DataModel dataModel) throws DMPMorphDefException {
		return fromAnything(morphScript, Optional.of(filterScript), Optional.of(dataModel));
	}

	default TransformationFlow fromAnything(
			final Reader morphScript,
			final Optional<Reader> filterScript,
			final Optional<DataModel> outputDataModel) throws DMPMorphDefException {

		final String morphContent;
		try {
			morphContent = CharStreams.toString(morphScript);
			morphScript.close();
		} catch (final IOException e) {
			throw new DMPMorphDefException("could not read morph string", e);
		}

		final Metamorph morph = createMorph(new StringReader(morphContent));
		final Optional<Filter> filter;
		if (filterScript.isPresent()) {
			filter = Optional.of(createFilter(filterScript.get()));
		} else {
			filter = Optional.absent();
		}

		return create(morph, morphContent, outputDataModel, filter);
	}
}
