package lorinc.pap;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Executable;

@SuppressWarnings("unused")
public class Agent {
    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.printf("Starting %s\n", Agent.class.getSimpleName());

        new AgentBuilder.Default()
                .type(ElementMatchers.any())
                .transform(new Transformer())
                .installOn(inst);
    }

    private static class Transformer implements AgentBuilder.Transformer {
        @Override
        public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module) {
            return builder.visit(
                    Advice.to(AroundAdvices.class)
                            .on(ElementMatchers.isConstructor())
            );
        }

        public static class AroundAdvices {
            @Advice.OnMethodEnter
            @SuppressWarnings("unused")
            public static void storeObjectAllocations(@Advice.Origin Executable executable, @Advice.AllArguments Object[] argsArray) {
                System.out.println(executable);
                System.out.println(argsArray);
            }
        }
    }
}