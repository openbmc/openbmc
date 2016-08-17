SUMMARY = "Devicekit power"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=756cf97871f77233638937da21b025d3"

DEPENDS = "libusb-compat libusb1 udev glib-2.0 dbus-glib polkit intltool-native libgudev"

SRC_URI = "http://upower.freedesktop.org/releases/DeviceKit-power-${PV}.tar.gz;name=archive"
SRC_URI[archive.md5sum] = "935d37f1e14b3c8a1d6dabcd9a38d3ca"
SRC_URI[archive.sha256sum] = "ad3e9a8bd9525d66fadc7fa53ef99e0632aa8cca8fd5bc27483956261153b373"

S = "${WORKDIR}/DeviceKit-power-${PV}"

inherit autotools pkgconfig

EXTRA_OECONF = " --with-backend=linux"

do_configure_prepend() {
    sed -i -e s:-nonet:\:g ${S}/doc/man/Makefile.am
    sed -i -e 's: doc : :g' ${S}/Makefile.am
}    

FILES_${PN} += "${datadir}/dbus-1/ \
                ${datadir}/polkit-1/ \
                ${base_libdir}/udev/* \
"

FILES_${PN}-dbg += "${base_libdir}/udev/.debug"



