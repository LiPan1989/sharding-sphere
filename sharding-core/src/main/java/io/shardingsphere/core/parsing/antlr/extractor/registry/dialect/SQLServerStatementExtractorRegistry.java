/*
 * Copyright 2016-2018 shardingsphere.io.
 * <p>
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
 * </p>
 */

package io.shardingsphere.core.parsing.antlr.extractor.registry.dialect;

import io.shardingsphere.core.parsing.antlr.extractor.SQLStatementExtractor;
import io.shardingsphere.core.parsing.antlr.extractor.SQLStatementType;
import io.shardingsphere.core.parsing.antlr.extractor.registry.SQLStatementExtractorRegistry;
import io.shardingsphere.core.parsing.antlr.extractor.sql.statement.AlterIndexExtractor;
import io.shardingsphere.core.parsing.antlr.extractor.sql.statement.CreateIndexExtractor;
import io.shardingsphere.core.parsing.antlr.extractor.sql.statement.CreateTableExtractor;
import io.shardingsphere.core.parsing.antlr.extractor.sql.statement.DropIndexExtractor;
import io.shardingsphere.core.parsing.antlr.extractor.sql.statement.TCLStatementExtractor;
import io.shardingsphere.core.parsing.antlr.extractor.sql.statement.dialect.sqlserver.SQLServerAlterTableExtractor;
import io.shardingsphere.core.parsing.antlr.extractor.sql.statement.dialect.sqlserver.SQLServerDropTableExtractor;
import io.shardingsphere.core.parsing.antlr.extractor.sql.statement.dialect.sqlserver.SQLServerTruncateTableExtractor;

import java.util.HashMap;
import java.util.Map;

/**
 * SQL statement extractor registry for SQLServer.
 * 
 * @author duhongjun
 * @author zhangliang
 */
public final class SQLServerStatementExtractorRegistry implements SQLStatementExtractorRegistry {
    
    private static final Map<SQLStatementType, SQLStatementExtractor> EXTRACTORS = new HashMap<>();
    
    static {
        registerDDL();
        registerTCL();
    }
    
    private static void registerDDL() {
        EXTRACTORS.put(SQLStatementType.CREATE_TABLE, new CreateTableExtractor());
        EXTRACTORS.put(SQLStatementType.ALTER_TABLE, new SQLServerAlterTableExtractor());
        EXTRACTORS.put(SQLStatementType.DROP_TABLE, new SQLServerDropTableExtractor());
        EXTRACTORS.put(SQLStatementType.TRUNCATE_TABLE, new SQLServerTruncateTableExtractor());
        EXTRACTORS.put(SQLStatementType.CREATE_INDEX, new CreateIndexExtractor());
        EXTRACTORS.put(SQLStatementType.ALTER_INDEX, new AlterIndexExtractor());
        EXTRACTORS.put(SQLStatementType.DROP_INDEX, new DropIndexExtractor());
    }
    
    private static void registerTCL() {
        EXTRACTORS.put(SQLStatementType.SET_TRANSACTION, new TCLStatementExtractor());
        EXTRACTORS.put(SQLStatementType.COMMIT, new TCLStatementExtractor());
        EXTRACTORS.put(SQLStatementType.ROLLBACK, new TCLStatementExtractor());
        EXTRACTORS.put(SQLStatementType.SAVEPOINT, new TCLStatementExtractor());
        EXTRACTORS.put(SQLStatementType.BEGIN_WORK, new TCLStatementExtractor());
    }
    
    @Override
    public SQLStatementExtractor getSQLStatementExtractor(final SQLStatementType type) {
        return EXTRACTORS.get(type);
    }
}
