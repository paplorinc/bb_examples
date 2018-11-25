Open the project in IntelliJ IDEA and run `Main - with agent`

The following error is displayed:
```
Error: A JNI error has occurred, please check your installation and try again
Exception in thread "main" Exception in thread "Monitor Ctrl-Break" 
Exception: java.lang.NoClassDefFoundError thrown from the UncaughtExceptionHandler in thread "Monitor Ctrl-Break"
```

It seems to fail because the bootstrap classloader cannot find the agent.