SUMMARY = "libebml is a C++ libary to parse EBML files"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://LICENSE.LGPL;md5=7fbc338309ac38fefcd64b04bb903e34"

SRC_URI = "\
    http://dl.matroska.org/downloads/libebml/libebml-${PV}.tar.bz2 \
    file://ldflags.patch \
    file://override-uname.patch \
"
SRC_URI[md5sum] = "efec729bf5a51e649e1d9d1f61c0ae7a"
SRC_URI[sha256sum] = "83b074d6b62715aa0080406ea84d33df2e44b5d874096640233a4db49b8096de"

inherit dos2unix

LIBEBML_OS = "Unknown"
LIBEBML_OS_linux = "Linux"
LIBEBML_OS_darwin = "Darwin"
LIBEBML_OS_mingw32 = "Windows"

EXTRA_OEMAKE = "\
    'TARGET_OS=${LIBEBML_OS}' \
    \
    'CXX=${CXX}' \
    'LD=${CXX}' \
    'AR=${AR}' \
    'RANLIB=${RANLIB}' \
    \
    'DEBUGFLAGS=' \
    'CPPFLAGS=${CPPFLAGS}' \
    'CXXFLAGS=${CXXFLAGS}' \
    'LDFLAGS=${LDFLAGS}' \
    \
    'prefix=${prefix}' \
    'libdir=${libdir}' \
    'includedir=${includedir}/ebml' \
"

do_compile () {
    oe_runmake -C make/linux
}

do_install() {
    cd ${S}/make/linux

    install -d ${D}${libdir}
    install -m 0644 libebml.a ${D}${libdir}
    install -m 0755 libebml.so.* ${D}${libdir}
    cp -R --no-dereference --preserve=mode,links -v libebml.so ${D}${libdir}

    install -d ${D}${includedir}/ebml
    for i in ../../ebml/*.h; do
        install -m 0644 $i ${D}${includedir}/ebml
    done

    install -d ${D}${includedir}/ebml/c
    for i in ../../ebml/c/*.h; do
        install -m 0644 $i ${D}${includedir}/ebml/c
    done
}
