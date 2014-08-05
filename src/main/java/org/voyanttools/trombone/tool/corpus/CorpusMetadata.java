/**
 * 
 */
package org.voyanttools.trombone.tool.corpus;

import java.io.IOException;

import org.voyanttools.trombone.lucene.StoredToLuceneDocumentsMapper;
import org.voyanttools.trombone.model.Corpus;
import org.voyanttools.trombone.storage.Storage;
import org.voyanttools.trombone.util.FlexibleParameters;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

/**
 * @author sgs
 *
 */
@XStreamAlias("corpus")
public class CorpusMetadata extends AbstractCorpusTool {

	@XStreamConverter(CorpusMetadataConverter.class)
	org.voyanttools.trombone.model.CorpusMetadata metadata = null;
	
	/**
	 * @param storage
	 * @param parameters
	 */
	public CorpusMetadata(Storage storage, FlexibleParameters parameters) {
		super(storage, parameters);
	}

	@Override
	protected void run(Corpus corpus, StoredToLuceneDocumentsMapper corpusMapper)
			throws IOException {
		metadata = corpus.getCorpusMetadata();
	}

}