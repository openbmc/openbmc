SUMMARY = "Host side USB console utilities"
DESCRIPTION = "Contains the lsusb utility for inspecting the devices connected to the USB bus."
HOMEPAGE = "http://www.linux-usb.org"
SECTION = "base"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "libusb zlib virtual/libiconv udev"

SRC_URI = "${KERNELORG_MIRROR}/linux/utils/usb/usbutils/usbutils-${PV}.tar.gz \
           file://usb-devices-avoid-dependency-on-bash.patch \
           file://Fix-NULL-pointer-crash.patch \
           file://iconv.patch \
          "

SRC_URI[md5sum] = "cb20148c2e784577e924a7b4c560c8fb"
SRC_URI[sha256sum] = "6d5f16c2961df37e22e492c736a3e162a8fde24480f23a40d85f79af80d3fe95"

inherit autotools gettext pkgconfig distro_features_check

FILES_${PN}-dev += "${datadir}/pkgconfig"

RRECOMMENDS_${PN} = "udev-hwdb"
RDEPENDS_${PN}-ptest = "libboost-system libboost-thread"
