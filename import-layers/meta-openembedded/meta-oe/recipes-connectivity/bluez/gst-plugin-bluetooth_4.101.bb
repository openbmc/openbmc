require bluez4.inc
require recipes-multimedia/gstreamer/gst-plugins-package.inc

PNBLACKLIST[gst-plugin-bluetooth] ?= "${@bb.utils.contains('DISTRO_FEATURES', 'bluez5', 'bluez5 conflicts with bluez4 and bluez5 is selected in DISTRO_FEATURES', '', d)}"

PR = "r1"

SRC_URI[md5sum] = "fb42cb7038c380eb0e2fa208987c96ad"
SRC_URI[sha256sum] = "59738410ade9f0e61a13c0f77d9aaffaafe49ba9418107e4ad75fe52846f7487"

DEPENDS = "bluez4 gst-plugins-base"

EXTRA_OECONF = "\
    --enable-gstreamer \
"

# clean unwanted files
do_install_append() {
    rm -rf ${D}${bindir}
    rm -rf ${D}${sbindir}
    rm -f  ${D}${libdir}/lib*
    rm -rf ${D}${libdir}/pkgconfig
    rm -rf ${D}${sysconfdir}
    rm -rf ${D}${base_libdir}
    rm -rf ${D}${libdir}/bluetooth
    rm -rf ${D}${localstatedir}
    rm -rf ${D}${libdir}/alsa-lib
    rm -rf ${D}${datadir}
    rm -rf ${D}${includedir}
    rm -rf ${D}${nonarch_base_libdir}
}

FILES_${PN} = "${libdir}/gstreamer-0.10/lib*.so"
FILES_${PN}-dev += "\
    ${libdir}/gstreamer-0.10/*.la \
"

FILES_${PN}-dbg += "\
    ${libdir}/*/.debug \
"

