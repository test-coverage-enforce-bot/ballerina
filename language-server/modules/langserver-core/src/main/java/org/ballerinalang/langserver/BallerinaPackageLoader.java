/**
 * Copyright (c) 2017, WSO2 Inc. (http://wso2.com) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ballerinalang.langserver;

import org.ballerinalang.compiler.CompilerPhase;
import org.ballerinalang.langserver.workspace.repository.NullSourceDirectory;
import org.ballerinalang.model.elements.PackageID;
import org.wso2.ballerinalang.compiler.PackageLoader;
import org.wso2.ballerinalang.compiler.SourceDirectory;
import org.wso2.ballerinalang.compiler.semantics.analyzer.CodeAnalyzer;
import org.wso2.ballerinalang.compiler.semantics.analyzer.SemanticAnalyzer;
import org.wso2.ballerinalang.compiler.tree.BLangPackage;
import org.wso2.ballerinalang.compiler.util.CompilerContext;
import org.wso2.ballerinalang.compiler.util.CompilerOptions;
import org.wso2.ballerinalang.compiler.util.Names;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.ballerinalang.compiler.CompilerOptionName.COMPILER_PHASE;
import static org.ballerinalang.compiler.CompilerOptionName.PRESERVE_WHITESPACE;
import static org.ballerinalang.compiler.CompilerOptionName.PROJECT_DIR;

/**
 * Loads the Ballerina builtin core and builtin packages.
 */
public class BallerinaPackageLoader {

    private static final int MAX_DEPTH = 10;

    /**
     * Get the Builtin Package.
     * @return {@link BLangPackage} Builtin BLang package
     */
    public static List<BLangPackage> getBuiltinPackages() {
        List<BLangPackage> builtins = new ArrayList<>();
        CompilerContext context = prepareCompilerContext();

        PackageLoader pkgLoader = PackageLoader.getInstance(context);
        SemanticAnalyzer semAnalyzer = SemanticAnalyzer.getInstance(context);
        CodeAnalyzer codeAnalyzer = CodeAnalyzer.getInstance(context);
        BLangPackage builtInPkg = codeAnalyzer
                .analyze(semAnalyzer.analyze(pkgLoader
                        .loadAndDefinePackage(Names.BUILTIN_ORG.value, Names.BUILTIN_PACKAGE.getValue())));
        builtins.add(builtInPkg);

        return builtins;
    }

    /**
     * Get the packages by name.
     *
     * @param name                  name of the package
     * @return {@link BLangPackage} blang package
     */
    static BLangPackage getPackageByName(CompilerContext context, String name) {
        PackageLoader pkgLoader = PackageLoader.getInstance(context);
        SemanticAnalyzer semAnalyzer = SemanticAnalyzer.getInstance(context);
        CodeAnalyzer codeAnalyzer = CodeAnalyzer.getInstance(context);
        String[] pkgComps = name.split("\\.");
        return codeAnalyzer.analyze(semAnalyzer.analyze(pkgLoader.loadAndDefinePackage(
                pkgComps[0], String.join(".", Arrays.copyOfRange(pkgComps, 1, pkgComps.length)))));
    }

    /**
     * Prepare a new compiler context.
     * @return {@link CompilerContext} Prepared compiler context
     */
    public static CompilerContext prepareCompilerContext() {
        CompilerContext context = new CompilerContext();
        CompilerOptions options = CompilerOptions.getInstance(context);
        options.put(PROJECT_DIR, "");
        options.put(COMPILER_PHASE, CompilerPhase.DESUGAR.toString());
        options.put(PRESERVE_WHITESPACE, "false");
        context.put(SourceDirectory.class, new NullSourceDirectory());

        return context;
    }

    /**
     * Get the package by ID via Package loader.
     * @param context               Compiler context
     * @param packageID             Package ID to resolve
     * @return {@link BLangPackage} Resolved BLang Package
     */
    public static BLangPackage getPackageById(CompilerContext context, PackageID packageID) {
        PackageLoader pkgLoader = PackageLoader.getInstance(context);
        return pkgLoader.loadAndDefinePackage(packageID);
    }

    /**
     * Get the packages set.
     * @param context       Current CompilerContext
     * @param maxDepth      Max depth to be searched
     * @return              {@link Set} set of packages
     */
    static Set<PackageID> getPackageList(CompilerContext context, int maxDepth) {
        PackageLoader pkgLoader = PackageLoader.getInstance(context);
        return pkgLoader.listPackages(Math.max(MAX_DEPTH, maxDepth));
    }
}
