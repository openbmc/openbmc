SUMMARY = "A mode switching tool for controlling 'flip flop' (multiple device) USB gear"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=091556bd6d0154cd4c2d17a1bfc7380a"

DEPENDS = "libusb1"

SRC_URI = "http://www.draisberghof.de/usb_modeswitch/${BP}.tar.bz2"
SRC_URI[md5sum] = "be73dcc84025794081a1d4d4e5a75e4c"
SRC_URI[sha256sum] = "c215236e6bada6e659fc195a31d611ea298a4bdb4d57a0d68c553b56585f8ba3"

inherit pkgconfig systemd

SYSTEMD_SERVICE_${PN} = "usb_modeswitch@.service"

EXTRA_OEMAKE = "TCL=${bindir}/tclsh"

FILES_${PN} = "${bindir} ${sysconfdir} ${nonarch_base_libdir}/udev/usb_modeswitch ${sbindir} ${localstatedir}/lib/usb_modeswitch"
RDEPENDS_${PN} = "tcl"
RRECOMMENDS_${PN} = "usb-modeswitch-data"

do_install() {
    oe_runmake DESTDIR=${D} install
    if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
        install -d ${D}/${systemd_unitdir}/system
        install -m 644 ${S}/usb_modeswitch@.service ${D}/${systemd_unitdir}/system
    fi
}
