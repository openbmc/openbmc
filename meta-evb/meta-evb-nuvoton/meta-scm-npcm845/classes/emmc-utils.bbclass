# Helper function for check emmc distro

def emmc_enabled(d, truevalue, falsevalue=""):
    if d.getVar("DISTRO") == 'arbel-scm-emmc':
        return truevalue
    if d.getVar("DISTRO") == 'arbel-scm-emmc-entity':
        return truevalue
    return falsevalue

