SUMMARY = "A mode switching tool for controlling 'flip flop' (multiple device) USB gear"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

DEPENDS = "libusb1"

SRC_URI = "http://www.draisberghof.de/usb_modeswitch/${BP}.tar.bz2"
SRC_URI[md5sum] = "f323fe700edd6ea404c40934ddf32b22"
SRC_URI[sha256sum] = "2752103de171ed5f6c8d6a6e3e73e16c9ee3e8e394dd39c5991f7680eb908a3a"

EXTRA_OEMAKE = "TCL=${bindir}/tclsh"

FILES_${PN} = "${bindir} ${sysconfdir} ${nonarch_base_libdir}/udev/usb_modeswitch ${sbindir} ${localstatedir}/lib/usb_modeswitch"
RDEPENDS_${PN} = "tcl"
RRECOMMENDS_${PN} = "usb-modeswitch-data"

do_install() {
	oe_runmake DESTDIR=${D} install
}
