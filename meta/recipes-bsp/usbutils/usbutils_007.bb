SUMMARY = "Host side USB console utilities"
DESCRIPTION = "Contains the lsusb utility for inspecting the devices connected to the USB bus."
HOMEPAGE = "http://www.linux-usb.org"
SECTION = "base"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

DEPENDS = "libusb zlib virtual/libiconv"

SRC_URI = "${KERNELORG_MIRROR}/linux/utils/usb/usbutils/usbutils-${PV}.tar.gz \
           file://usb-devices-avoid-dependency-on-bash.patch \
           file://Fix-NULL-pointer-crash.patch \
           file://iconv.patch \
          "

SRC_URI[md5sum] = "be6c42294be5c940f208190d3479d50c"
SRC_URI[sha256sum] = "e65c234cadf7c81b6b1567c440e3b9b31b44f51c27df3e45741b88848d8b37d3"

inherit autotools gettext pkgconfig

do_install_append() {
	# We only need the compressed copy, remove the uncompressed version
	rm -f ${D}${datadir}/usb.ids
}

PACKAGES += "${PN}-ids"
FILES_${PN}-dev += "${datadir}/pkgconfig"
FILES_${PN}-ids = "${datadir}/usb*"

RDEPENDS_${PN} = "${PN}-ids"
