SECTION = "e/libs"
LICENSE = "MIT & BSD"
DEPENDS += "pkgconfig-native"

# revision 0d93ec84b30bc1bee2caaee72d667f87bc468a70 made SRCDATE and hence PV go backwards, so we need to up PE to unbreak builds and feeds :(
PE = "2"

inherit e-base autotools

# evas-native looks at this var, so keep it

do_configure_prepend() {
    autopoint || touch config.rpath
}

do_install_prepend () {
    for i in `find ${B}/ -name "*.pc" -type f` ; do \
        sed -i -e 's:-L${STAGING_LIBDIR}:-L\$\{libdir\}:g' -e 's:-I${STAGING_LIBDIR}:-I\$\{libdir\}:g' -e 's:-I${STAGING_INCDIR}:-I\$\{includedir\}:g' $i
    done
}

PACKAGES =+ "${PN}-themes"
PACKAGES += "${PN}-tests"
PACKAGES += "${PN}-eolian"

FILES_${PN} = "${libdir}/*.so.* \
               ${libdir}/edje/modules/${PN}/*/module.so \
               ${libdir}/${PN}/plugins/*.so \
               ${datadir}/dbus-1/services/* \
"


FILES_${PN}-themes = "${datadir}/${PN}/themes \
                      ${datadir}/${PN}/data \
                      ${libdir}/${PN}/plugins/data/*.edj \
                      ${datadir}/${PN}/fonts \
                      ${datadir}/${PN}/pointers \
                      ${datadir}/${PN}/images \
                      ${datadir}/${PN}/users \
                      ${datadir}/${PN}/images \
                      ${datadir}/${PN}/styles"

FILES_${PN}-dev   += "${bindir}/${PN}-config \
                      ${libdir}/pkgconfig/* \
                      ${libdir}/lib*.la \
                      ${libdir}/*.so \
                      ${libdir}/${PN}/*.la \
                      ${libdir}/${PN}/*/*.la \
                      ${datadir}/${PN}/edje_externals \
                      ${libdir}/edje/modules/${PN}/*/module.la \
"

FILES_${PN}-eolian = " \
    ${datadir}/eolian/include \
"

FILES_${PN}-staticdev += "${libdir}/${BPN}/*/*.a"

FILES_${PN}-dbg +=   "${libdir}/${PN}/.debug \
                      ${libdir}/${PN}/*/.debug \
                      ${libdir}/edje/modules/${PN}/*/.debug/module.so \
"

FILES_${PN}-tests  = "${bindir}/${PN} \
                      ${bindir}/*_* \
                      ${datadir}/${PN}"
