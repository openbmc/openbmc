SUMMARY = "Dummy packages which handle excluding packages from the sdk, e.g. ensuring perl is excluded from buildtools"
LICENSE = "MIT"

PACKAGE_ARCH = "all"

inherit allarch

python() {
    # Put the package somewhere separate to ensure it's never used except
    # when we want it
    # (note that we have to do this in anonymous python here to avoid
    # allarch.bbclass disabling itself)
    d.setVar('PACKAGE_ARCH', '${DUMMYARCH}')
}

ALLOW_EMPTY_${PN} = "1"

PR[vardeps] += "DUMMYPROVIDES"
PR[vardeps] += "DUMMYPROVIDES_PACKAGES"

DUMMYPROVIDES_PACKAGES ??= ""
DUMMYPROVIDES_PACKAGES_MULTILIB = "${@' '.join([multilib_pkg_extend(d, pkg) for pkg in d.getVar('DUMMYPROVIDES_PACKAGES').split()])}"
DUMMYPROVIDES += "${DUMMYPROVIDES_PACKAGES_MULTILIB}"

python populate_packages_prepend() {
    p = d.getVar("PN")
    d.appendVar("RPROVIDES_%s" % p, "${DUMMYPROVIDES}")
    d.appendVar("RCONFLICTS_%s" % p, "${DUMMYPROVIDES}")
    d.appendVar("RREPLACES_%s" % p, "${DUMMYPROVIDES_PACKAGES_MULTILIB}")
}

