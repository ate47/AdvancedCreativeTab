package fr.atesab.act.internalcommand;

import java.lang.reflect.Method;

class AbstractInternalCommand {
    Class<?> module;
    String name;
    Method method;

    AbstractInternalCommand(Class<?> module, String name, Method method) {
        this.module = module;
        this.name = name;
        this.method = method;
    }

    String getCommandRealName() {
        return module.getName() + "#" + method.getName();
    }
}
