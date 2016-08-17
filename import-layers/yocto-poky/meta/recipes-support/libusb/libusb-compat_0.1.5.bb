SUMMARY = "libusb-0.1 compatibility layer for libusb1"
DESCRIPTION = "libusb-0.1 compatible layer for libusb1, a drop-in replacement \
that aims to look, feel and behave exactly like libusb-0.1"
HOMEPAGE = "http://www.libusb.org/"
BUGTRACKER = "http://www.libusb.org/report"
SECTION = "libs"

LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f2ac5f3ac4835e8f91324a26a590a423"
DEPENDS = "libusb1"

# Few packages are known not to work with libusb-compat (e.g. libmtp-1.0.0),
# so here libusb-0.1 is removed completely instead of adding virtual/libusb0.
# Besides, libusb-0.1 uses a per 1ms polling that hurts a lot to power
# consumption.
PROVIDES = "libusb virtual/libusb0"
BBCLASSEXTEND = "native nativesdk"

PE = "1"

SRC_URI = "${SOURCEFORGE_MIRROR}/libusb/libusb-compat-${PV}.tar.bz2 \
           file://0001-usb.h-Include-sys-types.h.patch \
          "

SRC_URI[md5sum] = "2780b6a758a1e2c2943bdbf7faf740e4"
SRC_URI[sha256sum] = "404ef4b6b324be79ac1bfb3d839eac860fbc929e6acb1ef88793a6ea328bc55a"

UPSTREAM_CHECK_URI = "http://sourceforge.net/projects/libusb/files/libusb-compat-0.1/"
UPSTREAM_CHECK_REGEX = "/libusb-compat-(?P<pver>(\d+[\.\-_]*)+)/$"

BINCONFIG = "${bindir}/libusb-config"

inherit autotools pkgconfig binconfig-disabled lib_package

EXTRA_OECONF = "--libdir=${base_libdir}"

do_install_append() {
	install -d ${D}${libdir}
	if [ ! ${D}${libdir} -ef ${D}${base_libdir} ]; then
		mv ${D}${base_libdir}/pkgconfig ${D}${libdir}
	fi
}
