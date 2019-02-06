SUMMARY = "A library for atomic integer operations"
HOMEPAGE = "https://github.com/ivmai/libatomic_ops/"
SECTION = "optional"
PROVIDES += "libatomics-ops"
LICENSE = "GPLv2 & MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://doc/LICENSING.txt;md5=e00dd5c8ac03a14c5ae5225a4525fa2d \
                    "

SRC_URI = "https://github.com/ivmai/libatomic_ops/releases/download/v${PV}/libatomic_ops-${PV}.tar.gz"

SRC_URI[md5sum] = "99128f05e3e3f4e0cd39aa23f23bbe0c"
SRC_URI[sha256sum] = "1d6a279edf81767e74d2ad2c9fce09459bc65f12c6525a40b0cb3e53c089f665"

S = "${WORKDIR}/libatomic_ops-${PV}"

ALLOW_EMPTY_${PN} = "1"

inherit autotools pkgconfig

BBCLASSEXTEND = "native nativesdk"
