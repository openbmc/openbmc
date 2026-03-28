SUMMARY = "Dummy packages which handle excluding packages from the sdk, e.g. ensuring perl is excluded from buildtools"
LICENSE = "MIT"

INHIBIT_DEFAULT_DEPS = "1"

python() {
    pkgdata_var = d.getVar("DUMMYSDK_PKGDATA_VARNAME")
    # Expand PKGDATA_DIR_SDK/SDK_SYS and stamp-extra-info before we lose the values
    d.setVar(pkgdata_var, d.getVar(pkgdata_var))
    stampinfo = d.getVar("DUMMYSDK_EXTRASTAMP_VARNAME")
    d.setVarFlag("do_packagedata", "stamp-extra-info", d.getVar(stampinfo))

    oe.utils.make_arch_independent(d)

    # Put the package somewhere separate to ensure it's never used except
    # when we want it
    # Need to set PACKAGE_ARCH after deferred nativesdk inherit
    d.setVar("PACKAGE_ARCH", "${DUMMYARCH}")
}

ALLOW_EMPTY:${PN} = "1"

PR[vardeps] += "DUMMYPROVIDES"
PR[vardeps] += "DUMMYPROVIDES_PACKAGES"

DUMMYPROVIDES_PACKAGES ??= ""
DUMMYPROVIDES_PACKAGES_MULTILIB = "${@' '.join([multilib_pkg_extend(d, pkg) for pkg in d.getVar('DUMMYPROVIDES_PACKAGES').split()])}"
DUMMYPROVIDES += "${DUMMYPROVIDES_PACKAGES_MULTILIB}"

python populate_packages:prepend() {
    p = d.getVar("PN")
    d.appendVar("RPROVIDES:%s" % p, "${DUMMYPROVIDES}")
    d.appendVar("RCONFLICTS:%s" % p, "${DUMMYPROVIDES}")
    d.appendVar("RREPLACES:%s" % p, "${DUMMYPROVIDES_PACKAGES_MULTILIB}")
}

deltask fetch
deltask unpack
deltask patch
deltask prepare_recipe_sysroot
deltask configure
deltask compile
deltask install
deltask populate_sysroot
