#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: GPL-2.0-only
#

class ClassRegistryMeta(type):
    """Give each ClassRegistry their own registry"""
    def __init__(cls, name, bases, attrs):
        cls.registry = {}
        type.__init__(cls, name, bases, attrs)

class ClassRegistry(type, metaclass=ClassRegistryMeta):
    """Maintain a registry of classes, indexed by name.

Note that this implementation requires that the names be unique, as it uses
a dictionary to hold the classes by name.

The name in the registry can be overridden via the 'name' attribute of the
class, and the 'priority' attribute controls priority. The prioritized()
method returns the registered classes in priority order.

Subclasses of ClassRegistry may define an 'implemented' property to exert
control over whether the class will be added to the registry (e.g. to keep
abstract base classes out of the registry)."""
    priority = 0
    def __init__(cls, name, bases, attrs):
        super(ClassRegistry, cls).__init__(name, bases, attrs)
        try:
            if not cls.implemented:
                return
        except AttributeError:
            pass

        try:
            cls.name
        except AttributeError:
            cls.name = name
        cls.registry[cls.name] = cls

    @classmethod
    def prioritized(tcls):
        return sorted(list(tcls.registry.values()),
                      key=lambda v: (v.priority, v.name), reverse=True)

    def unregister(cls):
        for key in cls.registry.keys():
            if cls.registry[key] is cls:
                del cls.registry[key]
