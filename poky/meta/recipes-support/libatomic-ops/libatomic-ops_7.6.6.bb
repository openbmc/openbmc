SUMMARY = "A library for atomic integer operations"
HOMEPAGE = "https://github.com/ivmai/libatomic_ops/"
SECTION = "optional"
PROVIDES += "libatomics-ops"
LICENSE = "GPLv2 & MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://doc/LICENSING.txt;md5=e00dd5c8ac03a14c5ae5225a4525fa2d \
                    "
PV .= "+git${SRCPV}"
SRCBRANCH ?= "release-7_6"

SRCREV = "76ffb3b87946e4c372d112d8d00786632deab934"
SRC_URI = "git://github.com/ivmai/libatomic_ops;branch=${SRCBRANCH}"

S = "${WORKDIR}/git"

ALLOW_EMPTY_${PN} = "1"

inherit autotools pkgconfig

BBCLASSEXTEND = "native nativesdk"
