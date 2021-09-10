SUMMARY = "PC/SC Lite smart card framework and applications"
HOMEPAGE = "http://pcsclite.alioth.debian.org/"
LICENSE = "BSD & GPLv3+"
LICENSE:${PN} = "BSD"
LICENSE:${PN}-lib = "BSD"
LICENSE:${PN}-doc = "BSD"
LICENSE:${PN}-dev = "BSD"
LICENSE:${PN}-dbg = "BSD & GPLv3+"
LICENSE:${PN}-spy = "GPLv3+"
LICENSE:${PN}-spy-dev = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=628c01ba985ecfa21677f5ee2d5202f6"

SRC_URI = "\
	https://pcsclite.apdu.fr/files/${BP}.tar.bz2 \
	file://0001-pcsc-spy-use-python3-only.patch \
"
SRC_URI[md5sum] = "eb595f2d398ff229207a6ec09fbc4e98"
SRC_URI[sha256sum] = "0148d403137124552c5d0f10f8cdab2cbb8dfc7c6ce75e018faf667be34f2ef9"

inherit autotools systemd pkgconfig perlnative

EXTRA_OECONF = " \
    --disable-libusb \
    --enable-usbdropdir=${libdir}/pcsc/drivers \
"

S = "${WORKDIR}/pcsc-lite-${PV}"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)} udev"
PACKAGECONFIG:class-native ??= ""

PACKAGECONFIG[systemd]  = ",--disable-libsystemd,systemd,"
PACKAGECONFIG[udev] = "--enable-libudev,--disable-libudev,udev"

PACKAGES = "${PN} ${PN}-dbg ${PN}-dev ${PN}-lib ${PN}-doc ${PN}-spy ${PN}-spy-dev"

RRECOMMENDS:${PN} = "ccid"
RRECOMMENDS:${PN}:class-native = ""
RPROVIDES:${PN}:class-native += "pcsc-lite-lib-native"

FILES:${PN} = "${sbindir}/pcscd"
FILES:${PN}-lib = "${libdir}/libpcsclite*${SOLIBS}"
FILES:${PN}-dev = "${includedir} \
                   ${libdir}/pkgconfig \
                   ${libdir}/libpcsclite.la \
                   ${libdir}/libpcsclite.so"

FILES:${PN}-spy = "${bindir}/pcsc-spy \
                   ${libdir}/libpcscspy*${SOLIBS}"
FILES:${PN}-spy-dev = "${libdir}/libpcscspy.la \
                       ${libdir}/libpcscspy.so "

RPROVIDES:${PN} += "${PN}-systemd"
RREPLACES:${PN} += "${PN}-systemd"
RCONFLICTS:${PN} += "${PN}-systemd"
SYSTEMD_SERVICE:${PN} = "pcscd.socket"
RDEPENDS:${PN}-spy +="python3"

BBCLASSEXTEND = "native"
