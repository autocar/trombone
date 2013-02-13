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
package org.voyanttools.trombone.tool;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.lucene.index.IndexReader;
import org.voyanttools.trombone.document.Metadata;
import org.voyanttools.trombone.document.StoredDocumentSource;
import org.voyanttools.trombone.input.source.InputSource;
import org.voyanttools.trombone.input.source.InputStreamInputSource;
import org.voyanttools.trombone.model.Corpus;
import org.voyanttools.trombone.model.CorpusMetadata;
import org.voyanttools.trombone.model.IndexedDocument;
import org.voyanttools.trombone.storage.Storage;
import org.voyanttools.trombone.storage.StoredDocumentSourceStorage;
import org.voyanttools.trombone.util.FlexibleParameters;

import edu.stanford.nlp.util.StringUtils;

/**
 * @author sgs
 *
 */
public class CorpusBuilder extends AbstractTool {

	private String storedId = null;

	/**
	 * @param storage
	 * @param parameters
	 */
	public CorpusBuilder(Storage storage, FlexibleParameters parameters) {
		super(storage, parameters);
	}

	/* (non-Javadoc)
	 * @see org.voyanttools.trombone.tool.RunnableTool#run()
	 */
	@Override
	public void run() throws IOException {
		String sid = parameters.getParameterValue("storedId");
		List<String> ids = storage.retrieveStrings(sid);
		StoredDocumentSourceStorage storedDocumentStorage = storage.getStoredDocumentSourceStorage();
		List<StoredDocumentSource> indexableStoredDocumentSources = new ArrayList<StoredDocumentSource>();
		for (String id : ids) {
			Metadata metadata = storedDocumentStorage.getStoredDocumentSourceMetadata(id);
			StoredDocumentSource storedDocumentSource = new StoredDocumentSource(id, metadata);
			indexableStoredDocumentSources.add(storedDocumentSource);
		}
		run(indexableStoredDocumentSources);
	}
	
	void run(List<StoredDocumentSource> storedDocumentSources) throws IOException {
		
		// build a hash set of the ids to check against the corpus
		Set<String> ids = new HashSet<String>();
		for (StoredDocumentSource sds : storedDocumentSources) {
			ids.add(sds.getId());
		}

		// first see if we can load an existing corpus
		if (parameters.containsKey("corpus")) {
			Corpus corpus = storage.getCorpusStorage().getCorpus(parameters.getParameterValue("corpus"));
			if (corpus!=null) {		
				
				// add documents that aren't in the corpus already
				List<StoredDocumentSource> corpusStoredDocumentSources = new ArrayList<StoredDocumentSource>();
				boolean overlap = true;
				for (IndexedDocument document : corpus) {
					String id = document.getId();
					if (ids.contains(id)==false) {
						overlap = false;
						corpusStoredDocumentSources.add(document.asStoredDocumentSource());
						ids.add(id);
					}
				}
				
				// we have overlap and the two sets are the same size, so just use the current corpus
				if (overlap && ids.size() == corpus.size()) {
					storedId = parameters.getParameterValue("corpus");
					return;
				}
				
				// we're adding document to an existing corpus, so prepend the corpus documents that aren't here
				storedDocumentSources.addAll(0, corpusStoredDocumentSources);
			}
		}
		
		StringBuilder sb = new StringBuilder();
		for (String id : ids) {
			sb.append(id);
		}
		
		storedId = DigestUtils.md5Hex(sb.toString());
		CorpusMetadata metadata = new CorpusMetadata(storedId);
		metadata.setDocumentIds(ids);
		Corpus corpus = new Corpus(storage, metadata);
		if (storage.getCorpusStorage().corpusExists(storedId)==false) {
			storage.getCorpusStorage().storeCorpus(corpus);
		}
		
		
		
		if (parameters.containsKey("corpus")==false) {
			parameters.addParameter("corpus", storedId);
		}

	}

	public String getStoredId() {
		return storedId;
	}

}
