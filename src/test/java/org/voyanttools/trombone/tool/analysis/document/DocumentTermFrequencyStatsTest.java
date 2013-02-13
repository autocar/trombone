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
package org.voyanttools.trombone.tool.analysis.document;

import static org.junit.Assert.*;

import org.apache.lucene.util.BytesRef;
import org.junit.Test;
import org.voyanttools.trombone.model.DocumentTermFrequencyStats;

/**
 * @author sgs
 *
 */
public class DocumentTermFrequencyStatsTest {

	@Test
	public void test() {
		DocumentTermFrequencyStats d1 = new DocumentTermFrequencyStats(1, "a", 1, 1, null, null);
		DocumentTermFrequencyStats d2 = new DocumentTermFrequencyStats(1, "z", 2, 2, null, null);
		DocumentTermFrequencyStats d3 = new DocumentTermFrequencyStats(1, "é", 3, 4, null, null);
		DocumentTermFrequencyStats d4 = new DocumentTermFrequencyStats(1, "a", 3, 3, null, null);
		DocumentTermFrequencyStatsQueue queue;

		// descending raw frequency, then ascending ascending alphabet
		queue = new DocumentTermFrequencyStatsQueue(2, DocumentTermFrequencyStatsSort.relativeFrequencyDesc);
		queue.insertWithOverflow(d1);
		queue.insertWithOverflow(d2);
		queue.insertWithOverflow(d3);
		queue.insertWithOverflow(d4);
		assertEquals(3, (int) queue.pop().getRelativeFrequency());
		assertEquals("é", queue.pop().getTerm());

		// descending raw frequency, then ascending ascending alphabet
		queue = new DocumentTermFrequencyStatsQueue(2, DocumentTermFrequencyStatsSort.relativeFrequencyAsc);
		queue.insertWithOverflow(d1);
		queue.insertWithOverflow(d2);
		queue.insertWithOverflow(d3);
		queue.insertWithOverflow(d4);
		assertEquals(2, (int) queue.pop().getRelativeFrequency());
		assertEquals("a", queue.pop().getTerm());

		// descending raw frequency, then ascending ascending alphabet
		queue = new DocumentTermFrequencyStatsQueue(2, DocumentTermFrequencyStatsSort.rawFrequencyDesc);
		queue.insertWithOverflow(d1);
		queue.insertWithOverflow(d2);
		queue.insertWithOverflow(d3);
		queue.insertWithOverflow(d4);
		assertEquals(3, queue.pop().getRawFrequency());
		assertEquals("a", queue.pop().getTerm());
		
		// ascending raw frequency, then ascending alphabet		
		queue = new DocumentTermFrequencyStatsQueue(2, DocumentTermFrequencyStatsSort.rawFrequencyAsc);
		queue.insertWithOverflow(d1);
		queue.insertWithOverflow(d2);
		queue.insertWithOverflow(d3);
		queue.insertWithOverflow(d4);
		assertEquals(2, queue.pop().getRawFrequency());
		assertEquals("a", queue.pop().getTerm());
		
		// ascending term alphabet, then descending term frequency
		queue = new DocumentTermFrequencyStatsQueue(2, DocumentTermFrequencyStatsSort.termAsc);
		queue.insertWithOverflow(d1);
		queue.insertWithOverflow(d2);
		queue.insertWithOverflow(d3);
		queue.insertWithOverflow(d4);
		assertEquals(1, queue.pop().getRawFrequency());
		assertEquals("a", queue.pop().getTerm());

		// descending term alphabet, then descending term frequency
		queue = new DocumentTermFrequencyStatsQueue(2, DocumentTermFrequencyStatsSort.termDesc);
		queue.insertWithOverflow(d1);
		queue.insertWithOverflow(d2);
		queue.insertWithOverflow(d3);
		queue.insertWithOverflow(d4);
		assertEquals(3, queue.pop().getRawFrequency());
		assertEquals("z", queue.pop().getTerm());

	}

}
