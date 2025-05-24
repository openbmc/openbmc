INHIBIT_DEFAULT_DEPS = "1"

FILES:${PN} = "${libexecdir} ${bindir}"

BINNAME = "${@d.getVar("BPN").strip("gcc-")}"

do_install() {
    install -d ${D}${bindir} ${D}${libexecdir}/${BP}/
    cp -r ${S}/. ${D}${libexecdir}/${BP}

    # Symlink all executables into bindir
    for f in ${D}${libexecdir}/${BP}/bin/*; do
        ln -rs $f ${D}${bindir}/$(basename $f)
    done
}

INSANE_SKIP:${PN} = "already-stripped libdir staticdev file-rdeps arch dev-so"

INHIBIT_SYSROOT_STRIP = "1"
INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

# Need to mark these as private until do_package's soname-finder only looks in $libdir
PRIVATE_LIBS = "libgcc_s.so.1 libstdc++.so.6"

BBCLASSEXTEND = "native nativesdk"

# Skipping file deps - we don't control the dependencies for prebuilt libraries, resulting in
# nothing provides libcrypt.so.1()(64bit) needed by nativesdk-gcc-arm-none-eabi 
# when packaged as RPM for SDK.
SKIP_FILEDEPS = "1"
