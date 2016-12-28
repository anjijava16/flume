/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flume.sink.hive;

import java.io.IOException;
import java.util.Collection;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.hive.hcatalog.streaming.HiveEndPoint;
import org.apache.hive.hcatalog.streaming.RecordWriter;
import org.apache.hive.hcatalog.streaming.StreamingException;
import org.apache.hive.hcatalog.streaming.StrictRegexWriter;
import org.apache.hive.hcatalog.streaming.TransactionBatch;

/**
 * Forwards the incoming event body to Hive unmodified Sets up the delimiter and
 * the field to column mapping
 */

public class HiveRegexSerializer implements HiveEventSerializer {
	public static final String ALIAS = "REGEX";

	public static final String defaultRegex = "(.*)";
	public static final String SERIALIZER_REGEX = "serializer.regex";
	private String regex;

	@Override
	public void write(TransactionBatch txnBatch, Event e) throws StreamingException, IOException, InterruptedException {
		txnBatch.write(e.getBody());
	}

	@Override
	public void write(TransactionBatch txnBatch, Collection<byte[]> events)
			throws StreamingException, IOException, InterruptedException {
		txnBatch.write(events);
	}

	@Override
	public RecordWriter createRecordWriter(HiveEndPoint endPoint)
			throws StreamingException, IOException, ClassNotFoundException {
		return new StrictRegexWriter(regex, endPoint, null);
	}

	@Override
	public void configure(Context context) {
		regex = context.getString(SERIALIZER_REGEX, defaultRegex);
		if (regex == null) {
			throw new IllegalArgumentException(
					"serializer.regex is not specified " + "for serializer " + this.getClass().getName());
		}
	}

}