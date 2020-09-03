// Copyright 2019-2020 Andrey Chalkin <L2jLiga> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package fastify_decorators.plugin.extensions

import com.intellij.lang.javascript.psi.ecmal4.JSAttributeListOwner
import fastify_decorators.plugin.CONTROLLER_DECORATOR_NAME

fun JSAttributeListOwner.hasDecorator(vararg decorators: String = arrayOf(CONTROLLER_DECORATOR_NAME)) =
    this.attributeList?.decorators?.find { decorators.contains(it.decoratorName) } != null