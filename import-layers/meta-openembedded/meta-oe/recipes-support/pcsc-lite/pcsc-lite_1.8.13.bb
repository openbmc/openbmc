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
LIC_FILES_CHKSUM = "file://COPYING;md5=bcfbd85230ac3c586fb294c8b627cf32"
DEPENDS = "udev"

SRC_URI = "https://alioth.debian.org/frs/download.php/file/4126/pcsc-lite-${PV}.tar.bz2"
SRC_URI[md5sum] = "4dcd22d20a6df8810fac5480cc320b6d"
SRC_URI[sha256sum] = "f315047e808d63a3262c4a040f77548af2e04d1fd707e0c2759369b926fbbc3b"


inherit autotools systemd pkgconfig

EXTRA_OECONF = " \
    --disable-libusb \
    --enable-libudev \
    --enable-usbdropdir=${libdir}/pcsc/drivers \
"

S = "${WORKDIR}/pcsc-lite-${PV}"

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
