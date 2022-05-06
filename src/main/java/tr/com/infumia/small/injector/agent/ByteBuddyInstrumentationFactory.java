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

package tr.com.infumia.small.injector.agent;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;
import tr.com.infumia.small.app.builder.ApplicationBuilder;
import tr.com.infumia.small.app.module.ModuleExtractor;
import tr.com.infumia.small.app.module.TemporaryModuleExtractor;
import tr.com.infumia.small.injector.loader.InjectableClassLoader;
import tr.com.infumia.small.injector.loader.InstrumentationInjectable;
import tr.com.infumia.small.injector.loader.IsolatedInjectableClassLoader;
import tr.com.infumia.small.injector.loader.manifest.JarManifestGenerator;
import tr.com.infumia.small.relocation.JarFileRelocator;
import tr.com.infumia.small.relocation.PassthroughRelocator;
import tr.com.infumia.small.relocation.RelocationRule;
import tr.com.infumia.small.relocation.Relocator;
import tr.com.infumia.small.relocation.facade.JarRelocatorFacadeFactory;
import tr.com.infumia.small.resolver.data.Dependency;
import tr.com.infumia.small.resolver.data.DependencyData;
import tr.com.infumia.small.resolver.data.Repository;
import tr.com.infumia.small.resolver.mirrors.SimpleMirrorSelector;
import tr.com.infumia.small.util.Packages;

public final class ByteBuddyInstrumentationFactory implements InstrumentationFactory {

  public static final String AGENT_JAR = "loader-agent.isolated-jar";

  private static final String AGENT_CLASS = "ClassLoaderAgent";

  private static final String AGENT_PACKAGE = "tr#com#infumia#small#injector#agent";

  private static final String BYTE_BUDDY_AGENT_CLASS = "net#bytebuddy#agent#ByteBuddyAgent";

  private final URL agentJarUrl;

  private final ModuleExtractor extractor;

  private final JarRelocatorFacadeFactory relocatorFacadeFactory;

  public ByteBuddyInstrumentationFactory(final JarRelocatorFacadeFactory relocatorFacadeFactory) {
    this.relocatorFacadeFactory = relocatorFacadeFactory;
    this.agentJarUrl = InstrumentationInjectable.class.getClassLoader().getResource(ByteBuddyInstrumentationFactory.AGENT_JAR);
    this.extractor = new TemporaryModuleExtractor();
  }

  public ByteBuddyInstrumentationFactory(final URL agentJarUrl, final ModuleExtractor extractor, final JarRelocatorFacadeFactory relocatorFacadeFactory) {
    this.agentJarUrl = agentJarUrl;
    this.extractor = extractor;
    this.relocatorFacadeFactory = relocatorFacadeFactory;
  }

  private static String generatePattern() {
    return String.format("small.%s", UUID.randomUUID());
  }

  private static DependencyData getDependency() throws MalformedURLException {
    final Dependency byteBuddy = new Dependency(
      "net.bytebuddy",
      "byte-buddy-agent",
      "1.11.0",
      null,
      new HashSet<>()
    );
    final Repository centralRepository = new Repository(new URL(SimpleMirrorSelector.CENTRAL_URL));
    return new DependencyData(
      Collections.emptySet(),
      Collections.singleton(centralRepository),
      Collections.singleton(byteBuddy)
    );
  }

  @Override
  public Instrumentation create() throws IOException, ReflectiveOperationException, URISyntaxException, NoSuchAlgorithmException {
    final URL extractedURL = this.extractor.extractModule(this.agentJarUrl, "loader-agent");
    final String pattern = ByteBuddyInstrumentationFactory.generatePattern();
    final String relocatedAgentClass = String.format("%s.%s", pattern, ByteBuddyInstrumentationFactory.AGENT_CLASS);
    final RelocationRule relocationRule = new RelocationRule(Packages.fix(ByteBuddyInstrumentationFactory.AGENT_PACKAGE), pattern, Collections.emptySet(), Collections.emptySet());
    final Relocator relocator = new JarFileRelocator(Collections.singleton(relocationRule), this.relocatorFacadeFactory);
    final File inputFile = new File(extractedURL.toURI());
    final File relocatedFile = File.createTempFile("small-agent", ".jar");
    final InjectableClassLoader classLoader = new IsolatedInjectableClassLoader();
    relocator.relocate(inputFile, relocatedFile);
    JarManifestGenerator.with(relocatedFile.toURI())
      .attribute("Manifest-Version", "1.0")
      .attribute("Agent-Class", relocatedAgentClass)
      .generate();
    ApplicationBuilder.injecting("Small-Agent", classLoader)
      .dataProviderFactory(dataUrl -> ByteBuddyInstrumentationFactory::getDependency)
      .relocatorFactory(rules -> new PassthroughRelocator())
      .relocationHelperFactory(rel -> (dependency, file) -> file)
      .build();
    final Class<?> byteBuddyAgentClass = Class.forName(Packages.fix(ByteBuddyInstrumentationFactory.BYTE_BUDDY_AGENT_CLASS), true, classLoader);
    final Method attachMethod = byteBuddyAgentClass.getMethod("attach", File.class, String.class, String.class);
    final Class<?> processHandle = Class.forName("java.lang.ProcessHandle");
    final Method currentMethod = processHandle.getMethod("current");
    final Method pidMethod = processHandle.getMethod("pid");
    final Object currentProcess = currentMethod.invoke(processHandle);
    final Long processId = (Long) pidMethod.invoke(currentProcess);
    attachMethod.invoke(null, relocatedFile, String.valueOf(processId), "");
    final Class<?> agentClass = Class.forName(relocatedAgentClass, true, ClassLoader.getSystemClassLoader());
    final Method instrMethod = agentClass.getMethod("getInstrumentation");
    return (Instrumentation) instrMethod.invoke(null);
  }
}
