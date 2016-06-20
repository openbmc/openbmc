import oe.maketype

def typed_value(key, d):
    """Construct a value for the specified metadata variable, using its flags
    to determine the type and parameters for construction."""
    var_type = d.getVarFlag(key, 'type', True)
    flags = d.getVarFlags(key)
    if flags is not None:
        flags = dict((flag, d.expand(value))
                     for flag, value in flags.iteritems())
    else:
        flags = {}

    try:
        return oe.maketype.create(d.getVar(key, True) or '', var_type, **flags)
    except (TypeError, ValueError), exc:
        bb.msg.fatal("Data", "%s: %s" % (key, str(exc)))
