DESCRIPTION = "C bindings for ZeroMQ"
HOMEPAGE = "http://www.zeromq.org"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9741c346eef56131163e13b9db1241b3"
DEPENDS = "zeromq"

SRC_URI = "https://github.com/zeromq/czmq/releases/download/v${PV}/czmq-${PV}.tar.gz \
    file://0001-Problem-out-of-date-with-zproject.patch \
"

SRC_URI[md5sum] = "6d3a6fdd25c2bb29897c53670dce97bf"
SRC_URI[sha256sum] = "f00ff419881dc2a05d0686c8467cd89b4882677fc56f31c0e2cc81c134cbb0c0"

inherit cmake

PACKAGES = "lib${BPN} lib${BPN}-dev lib${BPN}-staticdev ${PN} ${PN}-dbg"

FILES_${PN} = "${bindir}/*"
FILES_lib${BPN} = "${libdir}/*.so.*"
FILES_lib${BPN}-dev = "${libdir}/*.so ${libdir}/pkgconfig ${includedir}"
FILES_lib${BPN}-staticdev = "${libdir}/lib*.a"

RDEPENDS_lib${BPN}-dev = "zeromq-dev"

PACKAGECONFIG ??= "lz4 uuid"
PACKAGECONFIG[lz4] = ",-DCMAKE_DISABLE_FIND_PACKAGE_lz4=TRUE,lz4"
PACKAGECONFIG[uuid] = ",-DCMAKE_DISABLE_FIND_PACKAGE_uuid=TRUE,util-linux"

BBCLASSEXTEND = "nativesdk"

