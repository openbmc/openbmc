# Helper function for check entity-manager distro

# Usage example:
# EXTRA_OECONF:append = " \
#    ${@entity_enabled(d, '--enable-configure-dbus=yes')}"

def distro_enabled(d, distro, truevalue, falsevalue=""):
    if d.getVar("DISTRO") == distro:
        return truevalue
    else:
        return falsevalue

def entity_enabled(d, val, fval=""):
    return bb.utils.contains('DISTRO_FEATURES',
        'entity-manager', val, fval, d)

