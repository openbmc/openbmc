DESCRIPTION = "High-level C binding for 0MQ"
HOMEPAGE = "http://czmq.zeromq.org/"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9741c346eef56131163e13b9db1241b3"
DEPENDS = "zeromq"

SRC_URI = "https://github.com/zeromq/czmq/releases/download/v${PV}/czmq-${PV}.tar.gz"

SRC_URI[md5sum] = "7e09997db6ac3b25e8ed104053040722"
SRC_URI[sha256sum] = "cfab29c2b3cc8a845749758a51e1dd5f5160c1ef57e2a41ea96e4c2dcc8feceb"

UPSTREAM_CHECK_URI = "https://github.com/zeromq/${BPN}/releases"

inherit cmake

PACKAGES = "lib${BPN} lib${BPN}-dev lib${BPN}-staticdev ${PN} ${PN}-dbg"

FILES_${PN} = "${bindir}/*"
FILES_lib${BPN} = "${libdir}/*.so.*"
FILES_lib${BPN}-dev = "${libdir}/*.so ${libdir}/pkgconfig ${includedir} ${datadir}/cmake"
FILES_lib${BPN}-staticdev = "${libdir}/lib*.a"

RDEPENDS_lib${BPN}-dev = "zeromq-dev"

PACKAGECONFIG ??= "lz4 uuid curl ${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"
PACKAGECONFIG[lz4] = ",-DCMAKE_DISABLE_FIND_PACKAGE_lz4=TRUE,lz4"
PACKAGECONFIG[uuid] = ",-DCMAKE_DISABLE_FIND_PACKAGE_uuid=TRUE,util-linux"
PACKAGECONFIG[curl] = ",-DCMAKE_DISABLE_FIND_PACKAGE_libcurl=TRUE,curl"
PACKAGECONFIG[systemd] = ",-DCMAKE_DISABLE_FIND_PACKAGE_systemd=TRUE,systemd"

BBCLASSEXTEND = "nativesdk"

