SUMMARY = "Jpeg 2000 implementation"
HOMEPAGE = "https://jasper-software.github.io/jasper/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a80440d1d8f17d041c71c7271d6e06eb"

SRC_URI = "git://github.com/jasper-software/jasper.git;protocol=https;branch=master"
SRCREV = "fe00207dc10db1d7cc6f2757961c5c6bdfd10973"

CVE_CHECK_IGNORE += "\
    CVE-2015-8751 \
"

S = "${WORKDIR}/git"

inherit cmake

PACKAGECONFIG ??= ""
PACKAGECONFIG[jpeg] = "-DJAS_ENABLE_LIBJPEG=true, -DJAS_ENABLE_LIBJPEG=false, jpeg"
PACKAGECONFIG[opengl] = "-DJAS_ENABLE_OPENGL=true, -DJAS_ENABLE_OPENGL=false, freeglut"

EXTRA_OECMAKE:append = " -DJAS_ENABLE_SHARED=true"

do_install:append() {
    chrpath -d ${D}${bindir}/jasper
    chrpath -d ${D}${bindir}/imginfo
    chrpath -d ${D}${bindir}/imgcmp
    chrpath -d ${D}${libdir}/libjasper.so.*
}
