package lorinc.pap.agent;

import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassInjector.UsingInstrumentation;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Executable;
import java.nio.file.Files;
import java.util.AbstractMap;
import java.util.ArrayList;

import static java.util.Collections.singletonMap;
import static net.bytebuddy.asm.Advice.OnMethodEnter;
import static net.bytebuddy.asm.Advice.to;
import static net.bytebuddy.description.type.TypeDescription.ForLoadedType;
import static net.bytebuddy.dynamic.ClassFileLocator.ForClassLoader.read;
import static net.bytebuddy.dynamic.loading.ClassInjector.UsingInstrumentation.Target.BOOTSTRAP;
import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * <pre>
 * Following https://gist.github.com/raphw/34c0e2fffe2ee7b4f02f
 *
 * The following are displayed: {@code
 *      Enter lorinc.pap.agent.BootstrapAgent$LocalClass
 *      lorinc.pap.agent.BootstrapAgent$LocalClass@75f32542
 *      []
 *      Key=Value
 *      Enter java.lang.Shutdown$Lock
 *      Enter java.lang.Shutdown$Lock}
 * But `ArrayList` and `SimpleImmutableEntry` constructors are ignored for some reason.
 * </pre>
 */
public class BootstrapAgent {

    public static void main(String[] args) throws Exception {
        premain(null, ByteBuddyAgent.install());

        System.out.println(new LocalClass());

        System.out.println(new ArrayList<>());
        System.out.println(new AbstractMap.SimpleImmutableEntry<>("Key", "Value"));
    }

    public static void premain(String arg, Instrumentation instrumentation) throws Exception {
        Class<ConstructorInterceptor> interceptorClass = ConstructorInterceptor.class;

        File temp = Files.createTempDirectory("tmp").toFile();
        UsingInstrumentation.of(temp, BOOTSTRAP, instrumentation)
            .inject(singletonMap(new ForLoadedType(interceptorClass), read(interceptorClass)));

        new AgentBuilder.Default()
            //.with(AgentBuilder.Listener.StreamWriting.toSystemError())
            .enableBootstrapInjection(instrumentation, temp)
            .ignore(none())
            .type(any())
            .transform((builder, t, c, m) -> builder.visit(to(interceptorClass).on(isConstructor())))
            .installOn(instrumentation);
    }

    static class LocalClass {
    }

    public static class ConstructorInterceptor {
        @OnMethodEnter
        public static void enter(@Advice.Origin Executable executable) {
            System.out.println("Enter " + executable.getName());
        }
    }
}