DESCRIPTION = "High-level C binding for 0MQ"
HOMEPAGE = "http://czmq.zeromq.org/"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9741c346eef56131163e13b9db1241b3"
DEPENDS = "zeromq"

SRC_URI = "https://github.com/zeromq/czmq/releases/download/v${PV}/czmq-${PV}.tar.gz"

SRC_URI[md5sum] = "471e9ec120fc66a2fe2aae14359e3cfa"
SRC_URI[sha256sum] = "5d720a204c2a58645d6f7643af15d563a712dad98c9d32c1ed913377daa6ac39"

UPSTREAM_CHECK_URI = "https://github.com/zeromq/${BPN}/releases"

inherit cmake pkgconfig

PACKAGES = "lib${BPN} lib${BPN}-dev lib${BPN}-staticdev ${PN} ${PN}-dbg"

FILES:${PN} = "${bindir}/*"
FILES:lib${BPN} = "${libdir}/*.so.*"
FILES:lib${BPN}-dev = "${libdir}/*.so ${libdir}/pkgconfig ${includedir} ${datadir}/cmake"
FILES:lib${BPN}-staticdev = "${libdir}/lib*.a"

RDEPENDS:lib${BPN}-dev = "zeromq-dev"

PACKAGECONFIG ??= "lz4 uuid curl ${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"
PACKAGECONFIG[curl] = "-DCZMQ_WITH_LIBCURL=ON,-DCZMQ_WITH_LIBCURL=OFF,curl"
PACKAGECONFIG[httpd] = "-DCZMQ_WITH_LIBMICROHTTPD=ON,-DCZMQ_WITH_LIBMICROHTTPD=OFF,libmicrohttpd"
PACKAGECONFIG[lz4] = "-DCZMQ_WITH_LZ4=ON,-DCZMQ_WITH_LZ4=OFF,lz4"
PACKAGECONFIG[nss] = "-DCZMQ_WITH_NSS=ON,-DCZMQ_WITH_NSS=OFF,nss"
PACKAGECONFIG[systemd] = "-DCZMQ_WITH_SYSTEMD=ON,-DCZMQ_WITH_SYSTEMD=OFF,systemd"
PACKAGECONFIG[uuid] = "-DCZMQ_WITH_UUID=ON,-DCZMQ_WITH_UUID=OFF,util-linux"

do_install:append() {
        mkdir -p ${D}/${includedir}/${BPN}
        mv ${D}/${includedir}/sha1.h ${D}/${includedir}/${BPN}/.
}
