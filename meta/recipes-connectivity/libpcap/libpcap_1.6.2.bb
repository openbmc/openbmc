require libpcap.inc

SRC_URI += "file://aclocal.patch \
            file://libpcap-pkgconfig-support.patch \
           "
SRC_URI[md5sum] = "5f14191c1a684a75532c739c2c4059fa"
SRC_URI[sha256sum] = "5db3e2998f1eeba2c76da55da5d474248fe19c44f49e15cac8a796a2c7e19690"

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
