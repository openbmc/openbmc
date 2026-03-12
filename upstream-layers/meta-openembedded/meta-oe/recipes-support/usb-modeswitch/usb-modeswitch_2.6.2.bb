SUMMARY = "A mode switching tool for controlling 'flip flop' (multiple device) USB gear"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=091556bd6d0154cd4c2d17a1bfc7380a"

DEPENDS = "libusb1"

SRC_URI = "http://www.draisberghof.de/usb_modeswitch/${BP}.tar.bz2"
SRC_URI[sha256sum] = "f7abd337784a9d1bd39cb8a587518aff6f2a43d916145eafd80b1b8b7146db66"

inherit pkgconfig systemd

SYSTEMD_SERVICE:${PN} = "usb_modeswitch@.service"

EXTRA_OEMAKE = "TCL=${bindir}/tclsh"

FILES:${PN} = " \
    ${bindir} \
    ${sysconfdir} \
    ${nonarch_base_libdir}/udev/usb_modeswitch \
    ${sbindir} ${localstatedir}/lib/usb_modeswitch \
"
RDEPENDS:${PN} = "tcl"
RRECOMMENDS:${PN} = "usb-modeswitch-data"

do_install() {
    oe_runmake DESTDIR=${D} UDEVDIR=${D}/${nonarch_base_libdir}/udev install
    if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
        install -d ${D}/${systemd_unitdir}/system
        install -m 644 ${S}/usb_modeswitch@.service ${D}/${systemd_unitdir}/system
    fi
}
