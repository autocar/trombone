/*******************************************************************************
 * Trombone is a flexible text processing and analysis library used
 * primarily by Voyant Tools (voyant-tools.org).
 * 
 * Copyright (©) 2007-2012 Stéfan Sinclair & Geoffrey Rockwell
 * 
 * This file is part of Trombone.
 * 
 * Trombone is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Trombone is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Trombone.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.voyanttools.trombone;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.DocsAndPositionsEnum;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.AttributeImpl;
import org.apache.lucene.util.AttributeSource;
import org.voyanttools.trombone.input.expand.StoredDocumentSourceExpander;
import org.voyanttools.trombone.input.extract.StoredDocumentSourceExtractor;
import org.voyanttools.trombone.input.index.LuceneIndexer;
import org.voyanttools.trombone.input.source.InputSource;
import org.voyanttools.trombone.input.source.InputSourcesBuilder;
import org.voyanttools.trombone.model.StoredDocumentSource;
import org.voyanttools.trombone.storage.Storage;
import org.voyanttools.trombone.storage.file.FileStorage;
import org.voyanttools.trombone.storage.memory.MemoryStorage;
import org.voyanttools.trombone.tool.RunnableTool;
import org.voyanttools.trombone.tool.StepEnabledIndexedCorpusCreator;
import org.voyanttools.trombone.tool.ToolRunner;
import org.voyanttools.trombone.tool.ToolSerializer;
import org.voyanttools.trombone.util.FlexibleParameters;
import org.voyanttools.trombone.util.TestHelper;

/**
 * @author sgs
 *
 */
public class Controller {

	private FlexibleParameters parameters;
	private Storage storage;

	public Controller(FlexibleParameters parameters) {
		this(parameters.getParameterValue("storage","").equals("file") ? new FileStorage() : new MemoryStorage(), parameters);
	}

	public Controller(Storage storage, FlexibleParameters parameters) {
		this.storage = storage;
		this.parameters = parameters;
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		if (args == null) {
			throw new NullPointerException("illegal arguments");
		}

		final FlexibleParameters parameters = new FlexibleParameters(args);

		final Controller controller = new Controller(parameters);
		controller.run();
	}

	private void run() throws IOException {
		
		ToolRunner toolRunner = new ToolRunner(storage, parameters);
		toolRunner.run();
		ToolSerializer toolSerializer = new ToolSerializer(parameters, toolRunner);
		toolSerializer.run();
		
		
//		RunnableTool tool = new StepEnabledCorpusCreator(storage, parameters);
//		tool.run();

		/*
		
		
		if (InputSourcesBuilder.hasParameterSources(parameters)) {
			InputSourcesBuilder inputSourcesBuilder = new InputSourcesBuilder(parameters);
			List<InputSource> inputSources = inputSourcesBuilder.getInputSources();
			StoredDocumentSourceExpander sourcesExpander = new StoredDocumentSourceExpander(storage.getStoredDocumentSourceStorage(), parameters);
			List<StoredDocumentSource> expandedDocs = sourcesExpander.getExpandedStoredDocumentSources(inputSources);
			StoredDocumentSourceExtractor extractedBuilder = new StoredDocumentSourceExtractor(storage.getStoredDocumentSourceStorage(), parameters);
			List<StoredDocumentSource> extractedDocs = extractedBuilder.getExtractedStoredDocumentSources(expandedDocs);
			LuceneIndexer luceneIndexer = new LuceneIndexer(storage, parameters);
			luceneIndexer.index(extractedDocs);
			
			IndexReader reader = storage.getLuceneManager().getIndexReader();
			Terms terms = reader.getTermVector(0, "lemmatized-en");
			TermsEnum termsEnum = null;
			DocsAndPositionsEnum docsAndPositionsEnum = null;
			
			termsEnum = terms.iterator(termsEnum);
			while(termsEnum.next()!=null) {
//				AttributeSource attributes = termsEnum.attributes();
//				Iterator<AttributeImpl> attributesIterator = attributes.getAttributeImplsIterator();
//				AttributeImpl attributeImpl;
//				while (attributesIterator.hasNext()) {
//					attributeImpl = attributesIterator.next();
//					System.err.println(attributeImpl);
//				}
				docsAndPositionsEnum = termsEnum.docsAndPositions(null, docsAndPositionsEnum, true);
				for (int i=0, len=docsAndPositionsEnum.freq(); i<len; i++) {
					System.err.println(termsEnum.term().utf8ToString()+": "+docsAndPositionsEnum.startOffset());
					docsAndPositionsEnum.nextPosition();
					
				}
//				System.err.println(termsEnum.term().utf8ToString()+": "+docsAndPositionsEnum..freq());
//				System.err.println(docsAndPositionsEnum);
			}
		}
		*/
		
	}

}
