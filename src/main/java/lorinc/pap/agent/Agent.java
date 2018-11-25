package lorinc.pap.agent;

import net.bytebuddy.agent.builder.AgentBuilder;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Set;

import static net.bytebuddy.asm.Advice.*;
import static net.bytebuddy.matcher.ElementMatchers.*;

@SuppressWarnings("unused")
public class Agent {
    public static final Set<String> constructors = new HashSet<>();

    public static void premain(String arg, Instrumentation inst) throws Exception {
        new AgentBuilder.Default()
                //.disableClassFormatChanges()
                //.with(AgentBuilder.Listener.StreamWriting.toSystemError())
                //.ignore(nameStartsWith("com.intellij.").or(nameStartsWith("net.bytebuddy.").or(nameStartsWith("lorinc.pap.agent."))))
                .ignore(none())
                .type(any())
                .transform((builder, td, cl, m) -> builder.visit(to(ConstructorInterceptor.class).on(isConstructor())))
                .installOn(inst);
    }

    public static class ConstructorInterceptor {
        @OnMethodEnter
        public static void enter(@Origin Constructor constructor) {
            constructors.add(constructor.getName());
        }
    }
}