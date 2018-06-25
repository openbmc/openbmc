SUMMARY = "A mode switching tool for controlling 'flip flop' (multiple device) USB gear"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

DEPENDS = "libusb1"

SRC_URI = "http://www.draisberghof.de/usb_modeswitch/${BP}.tar.bz2"
SRC_URI[md5sum] = "38ad5c9d70e06227a00361bdc2b1e568"
SRC_URI[sha256sum] = "31c0be280d49a99ec3dc0be3325bef320d9c04b50714ef0ce1e36a614d687633"

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
