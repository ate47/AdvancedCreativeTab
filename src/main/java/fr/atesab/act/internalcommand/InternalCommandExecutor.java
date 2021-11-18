package fr.atesab.act.internalcommand;

import java.util.HashMap;
import java.util.Map;

public class InternalCommandExecutor {
    private final Map<String, AbstractInternalCommand> commands = new HashMap<>();

    public void registerModule(Class<?> cls) {
        var mic = cls.getAnnotation(InternalCommandModule.class);

        var prefix = mic != null ? (mic.useBaseName() ? cls.getCanonicalName() : mic.name()) : cls.getCanonicalName();

        if (!prefix.isEmpty()) {
            prefix += ".";
        }

        for (var method : cls.getDeclaredMethods()) {
            var ic = method.getAnnotation(InternalCommand.class);
            if (ic != null) {
                var name = ic.name();
                if (name.isEmpty()) {
                    name = method.getName();
                }

                name = prefix + name;

                var aic = new AbstractInternalCommand(name, method);

                var old = commands.get(name);

                if (old != null) {
                    throw new IllegalArgumentException("The method " + aic.getCommandRealName()
                            + " has the same @InternalCommand name as " + old.getCommandRealName());
                }

                commands.put(name, aic);
            }
        }
    }
}
