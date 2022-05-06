//
// MIT License
//
// Copyright (c) 2021 Vaishnav Anil
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//

package tr.com.infumia.small.app.builder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import tr.com.infumia.small.app.module.ModuleExtractor;
import tr.com.infumia.small.app.module.TemporaryModuleExtractor;
import tr.com.infumia.small.util.Modules;

public final class IsolationConfiguration {

  private final String applicationClass;

  private final ModuleExtractor moduleExtractor;

  private final Collection<String> modules;

  private final ClassLoader parentClassloader;

  public IsolationConfiguration(final String applicationClass, final Collection<String> modules, final ClassLoader parentClassloader, final ModuleExtractor moduleExtractor) {
    this.applicationClass = applicationClass;
    this.modules = Collections.unmodifiableCollection(modules);
    this.parentClassloader = parentClassloader;
    this.moduleExtractor = moduleExtractor;
  }

  public static Builder builder(final String applicationClass) {
    return new Builder().applicationClass(applicationClass);
  }

  public String getApplicationClass() {
    return this.applicationClass;
  }

  public ModuleExtractor getModuleExtractor() {
    return this.moduleExtractor;
  }

  public Collection<String> getModules() {
    return this.modules;
  }

  public ClassLoader getParentClassloader() {
    return this.parentClassloader;
  }

  public static final class Builder {

    private String applicationClass;

    private ModuleExtractor moduleExtractor;

    private Collection<String> modules = new HashSet<>();

    private ClassLoader parentClassloader;

    public Builder applicationClass(final String applicationClass) {
      this.applicationClass = applicationClass;
      return this;
    }

    public IsolationConfiguration build() throws IOException, URISyntaxException {
      return new IsolationConfiguration(this.getApplicationClass(), this.getModules(), this.getParentClassloader(), this.getModuleExtractor());
    }

    public Builder module(final String module) {
      this.modules.add(module);
      return this;
    }

    public Builder moduleExtractor(final ModuleExtractor moduleExtractor) {
      this.moduleExtractor = moduleExtractor;
      return this;
    }

    public Builder modules(final Collection<String> modules) {
      final Collection<String> mod = new HashSet<>(modules);
      mod.addAll(modules);
      this.modules = mod;
      return this;
    }

    public Builder parentClassLoader(final ClassLoader classLoader) {
      this.parentClassloader = classLoader;
      return this;
    }

    String getApplicationClass() {
      if (this.applicationClass == null) {
        throw new AssertionError("Application Class not Provided!");
      }
      return this.applicationClass;
    }

    ModuleExtractor getModuleExtractor() {
      if (this.moduleExtractor == null) {
        this.moduleExtractor = new TemporaryModuleExtractor();
      }
      return this.moduleExtractor;
    }

    Collection<String> getModules() throws IOException, URISyntaxException {
      if (this.modules == null || this.modules.isEmpty()) {
        this.modules = Modules.findLocalModules();
      }
      return this.modules;
    }

    ClassLoader getParentClassloader() {
      if (this.parentClassloader == null) {
        this.parentClassloader = ClassLoader.getSystemClassLoader().getParent();
      }
      return this.parentClassloader;
    }
  }
}
