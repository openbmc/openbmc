require ${BPN}.inc

DEPENDS = "zlib-native jpeg-native libpng-native libxext-native libxft-native"

inherit native

EXTRA_OECMAKE += " \
    -DOPTION_BUILD_SHARED_LIBS=OFF \
    -DOPTION_USE_THREADS=OFF \
    -DOPTION_USE_XDBE=OFF \
    -DOPTION_USE_XFT=OFF \
    -DOPTION_BUILD_EXAMPLES=OFF \
    -DOPTION_USE_XINERAMA=OFF \
    -DOPTION_USE_XFIXES=OFF \
    -DOPTION_USE_XCURSOR=OFF \
"

do_install_append() {
    # make sure native fltk-config is not used accidentaly
    rm -f ${D}${bindir}/fltk-config
}
