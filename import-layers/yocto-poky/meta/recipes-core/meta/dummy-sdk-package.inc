SUMMARY = "Dummy packages which handle excluding packages from the sdk, e.g. ensuring perl is excluded from buildtools"
LICENSE = "MIT"

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

python populate_packages_prepend() {
    p = d.getVar("PN")
    d.appendVar("RPROVIDES_%s" % p, "${DUMMYPROVIDES}")
    #d.appendVar("RCONFLICTS_%s" % p, "${DUMMYPROVIDES}")
    #d.appendVar("RREPLACES_%s" % p, "${DUMMYPROVIDES}")
}

