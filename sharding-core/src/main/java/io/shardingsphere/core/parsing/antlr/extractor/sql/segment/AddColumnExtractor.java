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

package io.shardingsphere.core.parsing.antlr.extractor.sql.segment;

import com.google.common.base.Optional;
import io.shardingsphere.core.parsing.antlr.extractor.sql.segment.result.ColumnDefinitionExtractResult;
import io.shardingsphere.core.parsing.antlr.extractor.sql.util.ASTUtils;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

/**
 * Add column clause extractor.
 *
 * @author duhongjun
 */
public class AddColumnExtractor implements SQLSegmentExtractor<Collection<ColumnDefinitionExtractResult>> {
    
    private final ColumnDefinitionPhraseExtractor columnDefinitionPhraseExtractor = new ColumnDefinitionPhraseExtractor();
    
    @Override
    public Collection<ColumnDefinitionExtractResult> extract(final ParserRuleContext ancestorNode) {
        Collection<ParserRuleContext> addColumnNodes = ASTUtils.getAllDescendantNodes(ancestorNode, RuleName.ADD_COLUMN);
        if (addColumnNodes.isEmpty()) {
            return Collections.emptyList();
        }
        Collection<ColumnDefinitionExtractResult> result = new LinkedList<>();
        for (ParserRuleContext each : addColumnNodes) {
            extractAddColumn(each, result);
        }
        return result;
    }
    
    private void extractAddColumn(final ParserRuleContext addColumnNode, final Collection<ColumnDefinitionExtractResult> result) {
        for (ParserRuleContext each : ASTUtils.getAllDescendantNodes(addColumnNode, RuleName.COLUMN_DEFINITION)) {
            Optional<ColumnDefinitionExtractResult> columnDefinition = columnDefinitionPhraseExtractor.extract(each);
            if (columnDefinition.isPresent()) {
                columnDefinition.get().setAdd(true);
                postExtractColumnDefinition(addColumnNode, columnDefinition.get());
                result.add(columnDefinition.get());
            }
        }
    }
    
    protected void postExtractColumnDefinition(final ParseTree ancestorNode, final ColumnDefinitionExtractResult columnDefinition) {
    }
}
