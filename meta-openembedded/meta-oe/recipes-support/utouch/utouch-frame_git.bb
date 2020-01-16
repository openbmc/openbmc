SUMMARY = "Touch Frame Library"
DESCRIPTION = "The frame library and tools are used to handle touch frames, i.e., collections of tracked contacts. Bindings for mtdev and XI2.1."
HOMEPAGE = "http://bitmath.org/code/frame/"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=2f31b266d3440dd7ee50f92cf67d8e6c"

DEPENDS += "mtdev utouch-evemu"

inherit autotools pkgconfig

SRC_URI = "git://bitmath.org/git/frame.git;protocol=http \
           file://remove-man-page-creation.patch \
           file://0001-include-sys-stat.h-for-fixing-build-issue-on-musl.patch \
           file://0001-Fix-build-on-32bit-arches-with-64bit-time_t.patch \
           "
SRCREV = "95363d5a1f7394d71144bf3b408ef4e6db4350fc"

PV = "1.1.2+git${SRCPV}"

S = "${WORKDIR}/git/"
