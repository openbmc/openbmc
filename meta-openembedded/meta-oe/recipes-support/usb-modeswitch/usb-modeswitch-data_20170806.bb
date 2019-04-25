SUMMARY = "Data files for usbmodeswitch"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

inherit allarch

DEPENDS += "tcl-native"

SRC_URI = "http://www.draisberghof.de/usb_modeswitch/${BP}.tar.bz2"
SRC_URI[md5sum] = "fb50d15b52e909d742dd16f0a9882316"
SRC_URI[sha256sum] = "ce413ef2a50e648e9c81bc3ea6110e7324a8bf981034fc9ec4467d3562563c2c"

do_install() {
    oe_runmake install DESTDIR=${D}
}

RDEPENDS_${PN} = "usb-modeswitch (>= 2.4.0)"
FILES_${PN} += "${base_libdir}/udev/rules.d/ \
                ${datadir}/usb_modeswitch"
