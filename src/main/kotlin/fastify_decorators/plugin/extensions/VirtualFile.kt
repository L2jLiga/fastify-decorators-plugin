// Copyright 2019-2020 Andrey Chalkin <L2jLiga> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package fastify_decorators.plugin.extensions

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.testFramework.LightVirtualFileBase

tailrec fun VirtualFile.getContext(): VirtualFile =
    if (this !is LightVirtualFileBase) this else this.originalFile.getContext()