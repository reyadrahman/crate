/*
 * Licensed to Crate under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Crate licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */

package io.crate.analyze;

import io.crate.analyze.symbol.Literal;
import io.crate.analyze.symbol.ParameterSymbol;
import io.crate.analyze.symbol.SelectSymbol;
import io.crate.analyze.symbol.Symbol;
import io.crate.data.Input;
import io.crate.data.Row;
import io.crate.metadata.Functions;
import io.crate.operation.BaseImplementationSymbolVisitor;
import io.crate.types.DataType;

import java.util.Map;

/**
 * Used to evaluate a symbol tree to a value.
 *
 * - This should be preferred over {@link EvaluatingNormalizer} if it's required to fully evaluate the tree
 * - This should be preferred over {@link io.crate.operation.InputFactory} if it's not used repeatedly.
 *   (Unless column evaluation is necessary)
 *
 * This does not handle Columns/InputColumns, only Functions, Literals, ParameterSymbols and SubQuery values
 */
public final class SymbolEvaluator extends BaseImplementationSymbolVisitor<Row> {

    private final Map<SelectSymbol, Object> subQueryValues;

    private SymbolEvaluator(Functions functions, Map<SelectSymbol, Object> subQueryValues) {
        super(functions);
        this.subQueryValues = subQueryValues;
    }

    public static Object evaluate(Functions functions,
                                  Symbol symbol,
                                  Row params,
                                  Map<SelectSymbol, Object> subQueryValues) {
        SymbolEvaluator symbolEval = new SymbolEvaluator(functions, subQueryValues);
        return symbolEval.process(symbol, params).value();
    }

    @Override
    public Input<?> visitParameterSymbol(ParameterSymbol parameterSymbol, Row params) {
        return () -> params.get(parameterSymbol.index());
    }

    @Override
    public Input<?> visitSelectSymbol(SelectSymbol selectSymbol, Row context) {
        DataType type = selectSymbol.valueType();
        return Literal.of(type, type.value(subQueryValues.get(selectSymbol)));
    }
}
