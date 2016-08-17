SUMMARY = "Data files for usbmodeswitch"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

inherit allarch

SRC_URI = "http://www.draisberghof.de/usb_modeswitch/${BP}.tar.bz2"
SRC_URI[md5sum] = "dff94177781298aaf0b3c2a3c3dea6b2"
SRC_URI[sha256sum] = "53889157937109e04dafe897c098ec94f3f44f9c0c83fc6ec8417aa9a587e536"

do_install() {
    oe_runmake install DESTDIR=${D}
}

RDEPENDS_${PN} = "usb-modeswitch (>= 2.2.0)"
FILES_${PN} += "${base_libdir}/udev/rules.d/ \
                ${datadir}/usb_modeswitch"
