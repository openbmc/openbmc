SUMMARY = "Jpeg 2000 implementation"
HOMEPAGE = "https://jasper-software.github.io/jasper/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=a80440d1d8f17d041c71c7271d6e06eb"

SRC_URI = "git://github.com/jasper-software/jasper.git;protocol=https;branch=master"
SRCREV = "917f7708b755d8434f70618108c1a76f1b6a0a82"

CVE_STATUS[CVE-2015-8751] = "fixed-version: The CPE in the NVD database doesn't reflect correctly the vulnerable versions."

S = "${WORKDIR}/git"

inherit cmake multilib_header

do_configure:prepend() {
	JAS_STDC_VERSION="$(echo __STDC_VERSION__ | ${CPP} -E -P -)"
}

EXTRA_OECMAKE:append = " -DJAS_STDC_VERSION=${JAS_STDC_VERSION}"

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'opengl x11', 'opengl', '', d)} \
		   jpeg"

PACKAGECONFIG[jpeg] = "-DJAS_ENABLE_LIBJPEG=ON,-DJAS_ENABLE_LIBJPEG=OFF,jpeg,"
PACKAGECONFIG[opengl] = "-DJAS_ENABLE_OPENGL=ON,-DJAS_ENABLE_OPENGL=OFF,freeglut,"

do_install:append() {
    chrpath -d ${D}${bindir}/jasper
    chrpath -d ${D}${bindir}/imginfo
    chrpath -d ${D}${bindir}/imgcmp
    chrpath -d ${D}${libdir}/libjasper.so.*
    oe_multilib_header jasper/jas_config.h
}

