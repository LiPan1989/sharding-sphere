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
import io.shardingsphere.core.parsing.antlr.extractor.sql.segment.result.ConditionExtractResult;
import io.shardingsphere.core.parsing.antlr.extractor.sql.util.ASTUtils;
import io.shardingsphere.core.parsing.antlr.extractor.sql.util.OperatorUtils;
import io.shardingsphere.core.parsing.parser.context.condition.AndCondition;
import io.shardingsphere.core.parsing.parser.context.condition.OrCondition;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

/**
 * Condition clause extractor.
 * 
 * @author duhongjun
 */
public final class ConditionExtractor implements SQLSegmentExtractor<Optional<ConditionExtractResult>> {
    
    @Override
    public Optional<ConditionExtractResult> extract(final ParserRuleContext ancestorNode) {
        Optional<ParserRuleContext> whereNode = ASTUtils.findFirstChildNode(ancestorNode, RuleName.WHERECLAUSE);
        if (!whereNode.isPresent()) {
            return Optional.absent();
        }
        return Optional.of(new ConditionExtractResult(extractCondition(whereNode.get()).get()));
    }
    
    private Optional<OrCondition> extractCondition(final ParseTree tree) {
        int index = -1;
        for (int i = 0; i < tree.getChildCount(); i++) {
            if (OperatorUtils.isRationalOperator(tree.getChild(i).getText())) {
                index = i;
                break;
            }
        }
        if (index > 0) {
            OrCondition result = new OrCondition();
            Optional<OrCondition> leftOrCondition = extractCondition(tree.getChild(index - 1));
            Optional<OrCondition> rightOrCondition = extractCondition(tree.getChild(index + 1));
            if (!leftOrCondition.get().getAndConditions().isEmpty() && !rightOrCondition.get().getAndConditions().isEmpty()) {
                if (OperatorUtils.isOr(tree.getChild(index).getText())) {
                    result.getAndConditions().addAll(leftOrCondition.get().getAndConditions());
                    result.getAndConditions().addAll(rightOrCondition.get().getAndConditions());
                } else if (OperatorUtils.isAnd(tree.getChild(index).getText())) {
                    for (AndCondition each : leftOrCondition.get().getAndConditions()) {
                        for (AndCondition eachRightOr : rightOrCondition.get().getAndConditions()) {
                            AndCondition tempList = new AndCondition();
                            tempList.getConditions().addAll(each.getConditions());
                            tempList.getConditions().addAll(eachRightOr.getConditions());
                            result.getAndConditions().add(tempList);
                        }
                    }
                }
                return Optional.of(result);
            } 
            if (!leftOrCondition.get().getAndConditions().isEmpty()) {
                return leftOrCondition;
            } 
            if (!rightOrCondition.get().getAndConditions().isEmpty()) {
                return rightOrCondition;
            }
        } else {
            index = -1;
            for (int i = 0; i < tree.getChildCount(); i++) {
                if (OperatorUtils.isStartParen(tree.getChild(i).getText())) {
                    index = i;
                    break;
                }
            }
            if (0 <= index) {
                if (tree.getChildCount() != index + 3) {
                    throw new RuntimeException("invalid expression");
                }
                if (OperatorUtils.parenMatch(tree.getChild(index + 2).getText(), tree.getChild(index).getText())) {
                    throw new RuntimeException("missing right paren");
                }
                if ("ExprContext".equals(tree.getChild(index + 1).getClass().getSimpleName())) {
                    return extractCondition(tree.getChild(index + 1));
                }
            } else {
                OrCondition result = new OrCondition();
                AndCondition newAndCondition = new AndCondition();
                //newAndCondition.getConditions().add(tree.getText());
                result.getAndConditions().add(newAndCondition);
                return Optional.of(result);
            }
        }
        return Optional.absent();
    }
}
