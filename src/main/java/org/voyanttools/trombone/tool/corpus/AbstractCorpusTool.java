/**
 * 
 */
package org.voyanttools.trombone.tool.corpus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.voyanttools.trombone.lucene.CorpusMapper;
import org.voyanttools.trombone.model.Corpus;
import org.voyanttools.trombone.model.CorpusAccess;
import org.voyanttools.trombone.model.CorpusAccessException;
import org.voyanttools.trombone.storage.Storage;
import org.voyanttools.trombone.tool.util.AbstractTool;
import org.voyanttools.trombone.util.FlexibleParameters;

/**
 * @author sgs
 *
 */
public abstract class AbstractCorpusTool extends AbstractTool {

	public AbstractCorpusTool(Storage storage, FlexibleParameters parameters) {
		super(storage, parameters);
	}

	/* (non-Javadoc)
	 * @see org.voyanttools.trombone.tool.utils.RunnableTool#run()
	 */
	@Override
	public void run() throws IOException {
		Corpus corpus = CorpusManager.getCorpus(storage, parameters);
		CorpusMapper corpusMapper = getCorpusMapper(corpus);
		run(corpusMapper);
	}
	
	protected CorpusMapper getCorpusMapper(Corpus corpus) throws IOException {
		if (this instanceof CorpusMetadata == false && this instanceof DocumentsMetadata == false) {
			CorpusAccess corpusAccess = corpus.getValidatedCorpusAccess(parameters);
			if (corpusAccess==CorpusAccess.NONCONSUMPTIVE && this instanceof ConsumptiveTool) {
				throw new CorpusAccessException("This tool isn't compatible with the limited access of this corpus.");
			}
		}
		return new CorpusMapper(storage, corpus);
	}
	
	protected List<String> getCorpusStoredDocumentIdsFromParameters(Corpus corpus) throws IOException {
		
		List<String> ids = new ArrayList<String>();
		
		// add IDs
		for (String docId : parameters.getParameterValues("docId")) {
			if (docId.isEmpty()==false) {
				for (String id : docId.split(",")) {
					ids.add(id.trim());
				}
			}
		}
		
		// add indices
		// check first if we have real values
		String[] docIndices = parameters.getParameterValues("docIndex");
		if (docIndices.length>0 && docIndices[0].isEmpty()==false) {
			for (String docIndex : docIndices) {
				for (String index : docIndex.split(",")) {
					int i = Integer.parseInt(index.trim());
					ids.add(corpus.getDocument(i).getId());
				}
			}
		}
		
		// no docs defined, so consider all
		if (ids.isEmpty()) {
			ids.addAll(corpus.getDocumentIds());
		}
		
		return ids;
		
	}
	
	protected CorpusMapper getStoredToLuceneDocumentsMapper(Corpus corpus) throws IOException {
		return new CorpusMapper(storage, corpus);
	}
	
	public abstract void run(CorpusMapper corpusMapper) throws IOException;


}
