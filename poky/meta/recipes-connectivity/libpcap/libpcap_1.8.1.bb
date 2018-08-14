require libpcap.inc

SRC_URI += " \
    file://libpcap-pkgconfig-support.patch \
    file://0001-Fix-compiler_state_t.ai-usage-when-INET6-is-not-defi.patch \
    file://0002-Add-missing-compiler_state_t-parameter.patch \
    file://disable-remote.patch \
    file://fix-grammar-deps.patch \
"

SRC_URI[md5sum] = "3d48f9cd171ff12b0efd9134b52f1447"
SRC_URI[sha256sum] = "673dbc69fdc3f5a86fb5759ab19899039a8e5e6c631749e48dcd9c6f0c83541e"

#
# make install doesn't cover the shared lib
# make install-shared is just broken (no symlinks)
#

do_configure_prepend () {
    #remove hardcoded references to /usr/include
    sed 's|\([ "^'\''I]\+\)/usr/include/|\1${STAGING_INCDIR}/|g' -i ${S}/configure.ac
}

do_install_prepend () {
    install -d ${D}${libdir}
    install -d ${D}${bindir}
    oe_runmake install-shared DESTDIR=${D}
    oe_libinstall -a -so libpcap ${D}${libdir}
    sed "s|@VERSION@|${PV}|" -i ${B}/libpcap.pc
    install -D -m 0644 libpcap.pc ${D}${libdir}/pkgconfig/libpcap.pc
}
