require ${BPN}.inc

DEPENDS = "zlib-native jpeg-native libpng-native libxext-native libxft-native"

inherit native

EXTRA_OECMAKE += " \
    -DOPTION_BUILD_SHARED_LIBS=OFF \
    -DOPTION_USE_THREADS=OFF \
    -DOPTION_USE_XDBE=OFF \
    -DOPTION_USE_XFT=OFF \
    -DFLTK_BUILD_TEST=OFF \
    -DOPTION_USE_XINERAMA=OFF \
    -DOPTION_USE_XFIXES=OFF \
    -DOPTION_USE_XCURSOR=OFF \
"

# lib/libfltk.a(Fl_Native_File_Chooser.cxx.o): undefined reference to symbol 'dlsym@@GLIBC_2.2.5'
LDFLAGS += "-ldl"

do_install:append() {
    # make sure native fltk-config is not used accidentaly
    rm -f ${D}${bindir}/fltk-config
}
