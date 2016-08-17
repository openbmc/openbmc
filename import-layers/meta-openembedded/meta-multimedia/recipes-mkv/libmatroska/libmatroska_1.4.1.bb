SUMMARY = "libmatroska is a C++ libary to parse Matroska files (.mkv and .mka)"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://LICENSE.LGPL;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = "libebml"

SRC_URI = "http://dl.matroska.org/downloads/${BPN}/${BPN}-${PV}.tar.bz2"
SRC_URI[md5sum] = "f61b2e5086f4bb9d24a43cc8af43a719"
SRC_URI[sha256sum] = "086f21873e925679babdabf793c3bb85c353d0cd79423543a3355e08e8a4efb7"

EXTRA_OEMAKE = "-e MAKEFLAGS="

do_compile() {
    cd ${S}/make/linux
    oe_runmake CROSS="${TARGET_PREFIX}"
}

do_install() {
    cd ${S}/make/linux

    install -d ${D}${libdir}
    install -m 0644 libmatroska.a ${D}${libdir}
    install -m 0755 libmatroska.so.* ${D}${libdir}
    cp -R --no-dereference --preserve=mode,links -v libmatroska.so ${D}${libdir}

    install -d ${D}${includedir}/matroska
    for i in ../../matroska/*.h; do
        install -m 0644 $i ${D}${includedir}/matroska
    done

    install -d ${D}${includedir}/matroska/c
    for i in ../../matroska/c/*.h; do
        install -m 0644 $i ${D}${includedir}/matroska/c
    done
}
