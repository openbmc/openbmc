"""OpenEmbedded variable typing support

Types are defined in the metadata by name, using the 'type' flag on a
variable.  Other flags may be utilized in the construction of the types.  See
the arguments of the type's factory for details.
"""

import inspect
import oe.types as types
import collections

available_types = {}

class MissingFlag(TypeError):
    """A particular flag is required to construct the type, but has not been
    provided."""
    def __init__(self, flag, type):
        self.flag = flag
        self.type = type
        TypeError.__init__(self)

    def __str__(self):
        return "Type '%s' requires flag '%s'" % (self.type, self.flag)

def factory(var_type):
    """Return the factory for a specified type."""
    if var_type is None:
        raise TypeError("No type specified. Valid types: %s" %
                        ', '.join(available_types))
    try:
        return available_types[var_type]
    except KeyError:
        raise TypeError("Invalid type '%s':\n  Valid types: %s" %
                        (var_type, ', '.join(available_types)))

def create(value, var_type, **flags):
    """Create an object of the specified type, given the specified flags and
    string value."""
    obj = factory(var_type)
    objflags = {}
    for flag in obj.flags:
        if flag not in flags:
            if flag not in obj.optflags:
                raise MissingFlag(flag, var_type)
        else:
            objflags[flag] = flags[flag]

    return obj(value, **objflags)

def get_callable_args(obj):
    """Grab all but the first argument of the specified callable, returning
    the list, as well as a list of which of the arguments have default
    values."""
    if type(obj) is type:
        obj = obj.__init__

    sig = inspect.signature(obj)
    args = list(sig.parameters.keys())
    defaults = list(s for s in sig.parameters.keys() if sig.parameters[s].default != inspect.Parameter.empty)
    flaglist = []
    if args:
        if len(args) > 1 and args[0] == 'self':
            args = args[1:]
        flaglist.extend(args)

    optional = set()
    if defaults:
        optional |= set(flaglist[-len(defaults):])
    return flaglist, optional

def factory_setup(name, obj):
    """Prepare a factory for use."""
    args, optional = get_callable_args(obj)
    extra_args = args[1:]
    if extra_args:
        obj.flags, optional = extra_args, optional
        obj.optflags = set(optional)
    else:
        obj.flags = obj.optflags = ()

    if not hasattr(obj, 'name'):
        obj.name = name

def register(name, factory):
    """Register a type, given its name and a factory callable.

    Determines the required and optional flags from the factory's
    arguments."""
    factory_setup(name, factory)
    available_types[factory.name] = factory


# Register all our included types
for name in dir(types):
    if name.startswith('_'):
        continue

    obj = getattr(types, name)
    if not isinstance(obj, collections.Callable):
        continue

    register(name, obj)
