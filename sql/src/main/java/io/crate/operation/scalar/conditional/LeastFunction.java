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

package io.crate.operation.scalar.conditional;

import io.crate.metadata.FunctionIdent;
import io.crate.metadata.FunctionInfo;
import io.crate.operation.scalar.ScalarFunctionModule;
import io.crate.types.DataType;
import io.crate.types.DataTypes;

import java.util.Arrays;

public class LeastFunction extends ConditionalCompareFunction {

    public static final String NAME = "least";

    public static final FunctionInfo TWO_LONG_INFO = new FunctionInfo(
        new FunctionIdent(NAME, Arrays.<DataType>asList(DataTypes.LONG, DataTypes.LONG)), DataTypes.LONG);

    private LeastFunction(FunctionInfo info) {
        super(info);
    }

    @Override
    public int compare(Object o1, Object o2) {
        return info().returnType().compareValueTo(o1, o2);
    }

    public static void register(ScalarFunctionModule module) {
        module.register(NAME, new ConditionalFunctionResolver(NAME, LeastFunction::new));
    }
}
