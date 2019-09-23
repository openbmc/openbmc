SUMMARY = "PC/SC Lite smart card framework and applications"
HOMEPAGE = "http://pcsclite.alioth.debian.org/"
LICENSE = "BSD & GPLv3+"
LICENSE_${PN} = "BSD"
LICENSE_${PN}-lib = "BSD"
LICENSE_${PN}-doc = "BSD"
LICENSE_${PN}-dev = "BSD"
LICENSE_${PN}-dbg = "BSD & GPLv3+"
LICENSE_${PN}-spy = "GPLv3+"
LICENSE_${PN}-spy-dev = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=628c01ba985ecfa21677f5ee2d5202f6"
DEPENDS = "udev"

SRC_URI = "https://pcsclite.apdu.fr/files/${BP}.tar.bz2"
SRC_URI[md5sum] = "c20650a36062ab1689f37f3302c988f2"
SRC_URI[sha256sum] = "d76d79edc31cf76e782b9f697420d3defbcc91778c3c650658086a1b748e8792"

inherit autotools systemd pkgconfig

EXTRA_OECONF = " \
    --disable-libusb \
    --enable-libudev \
    --enable-usbdropdir=${libdir}/pcsc/drivers \
"

S = "${WORKDIR}/pcsc-lite-${PV}"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"

PACKAGECONFIG[systemd]  = ",--disable-libsystemd,systemd,"

PACKAGES = "${PN} ${PN}-dbg ${PN}-dev ${PN}-lib ${PN}-doc ${PN}-spy ${PN}-spy-dev"

RRECOMMENDS_${PN} = "ccid"

FILES_${PN} = "${sbindir}/pcscd"
FILES_${PN}-lib = "${libdir}/libpcsclite*${SOLIBS}"
FILES_${PN}-dev = "${includedir} \
                   ${libdir}/pkgconfig \
                   ${libdir}/libpcsclite.la \
                   ${libdir}/libpcsclite.so"

FILES_${PN}-spy = "${bindir}/pcsc-spy \
                   ${libdir}/libpcscspy*${SOLIBS}"
FILES_${PN}-spy-dev = "${libdir}/libpcscspy.la \
                       ${libdir}/libpcscspy.so "

RPROVIDES_${PN} += "${PN}-systemd"
RREPLACES_${PN} += "${PN}-systemd"
RCONFLICTS_${PN} += "${PN}-systemd"
SYSTEMD_SERVICE_${PN} = "pcscd.socket"
RDEPENDS_${PN}-spy +="python"
