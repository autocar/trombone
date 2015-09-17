/**
 * 
 */
package org.voyanttools.trombone.tool.corpus;

import java.io.IOException;

import org.voyanttools.trombone.model.Corpus;
import org.voyanttools.trombone.model.CorpusAliasDB;
import org.voyanttools.trombone.storage.Migrator;
import org.voyanttools.trombone.storage.Storage;
import org.voyanttools.trombone.tool.build.RealCorpusCreator;
import org.voyanttools.trombone.tool.utils.AbstractTool;
import org.voyanttools.trombone.util.FlexibleParameters;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
 * @author sgs
 *
 */
@XStreamAlias("corpus")
public class CorpusManager extends AbstractTool {
	
	private String id = "";
	
	@XStreamOmitField
	private Corpus corpus = null;

	public CorpusManager(Storage storage, FlexibleParameters parameters) {
		super(storage, parameters);
	}

	/* (non-Javadoc)
	 * @see org.voyanttools.trombone.tool.utils.RunnableTool#run()
	 */
	@Override
	public void run() throws IOException {
		
		// try to load a corpus
		if (parameters.containsKey("corpus")) {
			String corpusId = parameters.getParameterValue("corpus");
			
			// lookup for an alias
			if (corpusId.length()<32 && CorpusAliasDB.exists(storage)) { // MD5 should be 32 characters
				CorpusAliasDB corpusAliasDB = new CorpusAliasDB(storage, true);
				String id = corpusAliasDB.get(corpusId);
				if (id!=null && id.isEmpty()==false) {
					corpusId = id;
				}
				corpusAliasDB.close();
			}
			
			// check if corpus exists and return it if so
			if (storage.getCorpusStorage().corpusExists(corpusId)) {
				this.id = corpusId;
				checkActions();
				return;
			}
			
			// check of a previous format exists and return it if so
			Migrator migrator = storage.getMigrator(corpusId);
			if (migrator!=null) {
				this.id = migrator.getMigratedCorpusId();
				checkActions();
				return;
			}
			
		}
		
		RealCorpusCreator realCorpusCreator = new RealCorpusCreator(storage, parameters);
		realCorpusCreator.run();
		this.id = realCorpusCreator.getStoredId();
		checkActions();
	}
	
	public static Corpus getCorpus(Storage storage, FlexibleParameters parameters) throws IOException {
		CorpusManager corpusManager = new CorpusManager(storage, parameters);
		corpusManager.run();
		return corpusManager.getCorpus();
	}
	
	private void checkActions() {
		if (parameters.containsKey("addAlias")) {
			CorpusAliasDB corpusAliasDB = new CorpusAliasDB(storage, false);
			corpusAliasDB.put(parameters.getParameterValue("addAlias"), this.id);
			corpusAliasDB.close();
		}
	}

	private Corpus getCorpus() throws IOException {
		if (this.corpus==null && this.id!=null) {
			this.corpus = this.storage.getCorpusStorage().getCorpus(this.id);
		}
		return this.corpus;
	}

}
