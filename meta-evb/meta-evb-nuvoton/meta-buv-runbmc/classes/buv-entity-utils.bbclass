# Helper function for check buv-entity distro

# Usage example:
# EXTRA_OECONF_append = " \
#    ${@entity_enabled(d, '--enable-configure-dbus=yes')}"

def distro_enabled(d, distro, truevalue, falsevalue=""):
    if d.getVar("DISTRO") == distro:
        return truevalue
    else:
        return falsevalue

def entity_enabled(d, val, fval=""):
    return distro_enabled(d, "buv-entity", val, fval)

