SUMMARY = "Data files for usbmodeswitch"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

inherit allarch

DEPENDS += "tcl-native"

SRC_URI = "http://www.draisberghof.de/usb_modeswitch/${BP}.tar.bz2"
SRC_URI[md5sum] = "0cc107cd0c4c83df0d9400c999e21dfd"
SRC_URI[sha256sum] = "e2dcfd9d28928d8d8f03381571a23442b3c50d48d343bc40a1a07d01662738d1"

do_install() {
    oe_runmake install DESTDIR=${D}
}

RDEPENDS_${PN} = "usb-modeswitch (>= 2.4.0)"
FILES_${PN} += "${base_libdir}/udev/rules.d/ \
                ${datadir}/usb_modeswitch"
