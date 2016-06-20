require libpcap.inc

SRC_URI += "file://aclocal.patch \
            file://libpcap-pkgconfig-support.patch \
           "
SRC_URI[md5sum] = "b2e13142bbaba857ab1c6894aedaf547"
SRC_URI[sha256sum] = "7ad3112187e88328b85e46dce7a9b949632af18ee74d97ffc3f2b41fe7f448b0"

#
# make install doesn't cover the shared lib
# make install-shared is just broken (no symlinks)
#

do_configure_prepend () {
    #remove hardcoded references to /usr/include
    sed 's|\([ "^'\''I]\+\)/usr/include/|\1${STAGING_INCDIR}/|g' -i ${S}/configure.in
}

do_install_prepend () {
    install -d ${D}${libdir}
    install -d ${D}${bindir}
    oe_runmake install-shared DESTDIR=${D}
    oe_libinstall -a -so libpcap ${D}${libdir}
    sed "s|@VERSION@|${PV}|" -i ${B}/libpcap.pc
    install -D -m 0644 libpcap.pc ${D}${libdir}/pkgconfig/libpcap.pc
}
