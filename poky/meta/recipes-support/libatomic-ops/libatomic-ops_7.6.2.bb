SUMMARY = "A library for atomic integer operations"
HOMEPAGE = "https://github.com/ivmai/libatomic_ops/"
SECTION = "optional"
PROVIDES += "libatomics-ops"
LICENSE = "GPLv2 & MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
                    file://doc/LICENSING.txt;md5=e00dd5c8ac03a14c5ae5225a4525fa2d \
		   "
PV .= "+git${SRCPV}"
SRCBRANCH ?= "release-7_6"

SRCREV = "5ae4b4aeea2baf13752d07e3038c47f70f06dcac"
SRC_URI = "git://github.com/ivmai/libatomic_ops;branch=${SRCBRANCH}"

S = "${WORKDIR}/git"

ALLOW_EMPTY_${PN} = "1"

inherit autotools pkgconfig

BBCLASSEXTEND = "native nativesdk"
