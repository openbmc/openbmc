SUMMARY = "Data files for usbmodeswitch"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

inherit allarch

DEPENDS += "tcl-native"

SRC_URI = "http://www.draisberghof.de/usb_modeswitch/${BP}.tar.bz2"
SRC_URI[md5sum] = "e8fce7eb949cbe16c61fb71bade4cc17"
SRC_URI[sha256sum] = "3f039b60791c21c7cb15c7986cac89650f076dc274798fa242231b910785eaf9"

do_install() {
    oe_runmake install DESTDIR=${D} RULESDIR=${D}/${nonarch_base_libdir}/udev/rules.d
}

RDEPENDS_${PN} = "usb-modeswitch (>= 2.4.0)"
FILES_${PN} += "${nonarch_base_libdir}/udev/rules.d/ \
                ${datadir}/usb_modeswitch"
