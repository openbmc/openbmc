SUMMARY = "PC/SC Lite smart card framework and applications"
HOMEPAGE = "https://pcsclite.apdu.fr/"
LICENSE = "BSD-3-Clause & GPL-3.0-or-later"
LICENSE:${PN} = "BSD-3-Clause"
LICENSE:${PN}-lib = "BSD-3-Clause"
LICENSE:${PN}-doc = "BSD-3-Clause"
LICENSE:${PN}-dev = "BSD-3-Clause"
LICENSE:${PN}-dbg = "BSD-3-Clause & GPL-3.0-or-later"
LICENSE:${PN}-spy = "GPL-3.0-or-later"
LICENSE:${PN}-spy-dev = "GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=628c01ba985ecfa21677f5ee2d5202f6"
DEPENDS = "autoconf-archive-native flex-native"

SRC_URI = "https://pcsclite.apdu.fr/files/${BP}.tar.bz2"
SRC_URI[sha256sum] = "5edcaf5d4544403bdab6ee2b5d6c02c6f97ea64eebf0825b8d0fa61ba417dada"

inherit autotools systemd pkgconfig perlnative

EXTRA_OECONF = " \
    --disable-libusb \
    --enable-usbdropdir=${libdir}/pcsc/drivers \
"

S = "${WORKDIR}/pcsc-lite-${PV}"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd polkit', d)} udev"
PACKAGECONFIG:class-native ??= ""

PACKAGECONFIG[systemd]  = ",--disable-libsystemd,systemd,"
PACKAGECONFIG[udev] = "--enable-libudev,--disable-libudev,udev"
PACKAGECONFIG[polkit] = ",--disable-polkit,polkit"

PACKAGES = "${PN} ${PN}-dbg ${PN}-dev ${PN}-lib ${PN}-doc ${PN}-spy ${PN}-spy-dev"

RRECOMMENDS:${PN} = "ccid"
RRECOMMENDS:${PN}:class-native = ""
RPROVIDES:${PN}:class-native += "pcsc-lite-lib-native"

FILES:${PN} = "${sbindir}/pcscd \
               ${datadir}/polkit-1"
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
RDEPENDS:${PN}-spy += "python3-core"

BBCLASSEXTEND = "native"
