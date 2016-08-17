SUMMARY = "libebml is a C++ libary to parse EBML files"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://LICENSE.LGPL;md5=f14599a2f089f6ff8c97e2baa4e3d575"

SRC_URI = "http://dl.matroska.org/downloads/libebml/libebml-${PV}.tar.bz2"
SRC_URI[md5sum] = "efec729bf5a51e649e1d9d1f61c0ae7a"
SRC_URI[sha256sum] = "83b074d6b62715aa0080406ea84d33df2e44b5d874096640233a4db49b8096de"

EXTRA_OEMAKE = "-e MAKEFLAGS="

do_compile() {
    cd ${S}/make/linux
    oe_runmake CROSS="${TARGET_PREFIX}"
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
