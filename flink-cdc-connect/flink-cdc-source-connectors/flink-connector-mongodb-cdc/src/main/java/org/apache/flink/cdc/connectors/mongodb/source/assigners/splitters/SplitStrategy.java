/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.cdc.connectors.mongodb.source.assigners.splitters;

import org.apache.flink.cdc.common.annotation.Internal;
import org.apache.flink.cdc.connectors.base.source.meta.split.SnapshotSplit;
import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.types.logical.RowType;

import org.bson.BsonDocument;

import java.util.Collection;

/**
 * The {@link MongoDBChunkSplitter} used to split collection into a set of chunks for MongoDB data
 * source.
 */
@Internal
public interface SplitStrategy {

    Collection<SnapshotSplit> split(SplitContext splitContext);

    default RowType shardKeysToRowType(BsonDocument shardKeys) {
        return shardKeysToRowType(shardKeys.keySet());
    }

    default RowType shardKeysToRowType(Collection<String> shardKeys) {
        DataTypes.Field[] fields =
                shardKeys.stream()
                        // We cannot get the exact type of the shard key, only the ordering of the
                        // shard index.
                        // Use the INT type as a placeholder.
                        .map(key -> DataTypes.FIELD(key, DataTypes.INT()))
                        .toArray(DataTypes.Field[]::new);
        return (RowType) DataTypes.ROW(fields).getLogicalType();
    }
}
