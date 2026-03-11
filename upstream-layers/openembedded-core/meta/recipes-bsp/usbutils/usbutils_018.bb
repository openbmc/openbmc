SUMMARY = "Host side USB console utilities"
DESCRIPTION = "Contains the lsusb utility for inspecting the devices connected to the USB bus."
HOMEPAGE = "http://www.linux-usb.org"
SECTION = "base"

LICENSE = "GPL-2.0-or-later & (GPL-2.0-only | GPL-3.0-only) & CC0-1.0 & LGPL-2.1-or-later & MIT"
LIC_FILES_CHKSUM = "file://LICENSES/CC0-1.0.txt;md5=cf1af55fc6f5b9a23e12086005298dcd \
		    file://LICENSES/GPL-2.0-only.txt;md5=c89d4ad08368966d8df5a90ea96bebe4 \
		    file://LICENSES/GPL-2.0-or-later.txt;md5=c89d4ad08368966d8df5a90ea96bebe4 \
		    file://LICENSES/GPL-3.0-only.txt;md5=050f496cfea7876fc13cdea643e041e0 \
		    file://LICENSES/LGPL-2.1-or-later.txt;md5=8c6e7513c570546f65ae570dae278c17 \
		    file://LICENSES/MIT.txt;md5=e8f57dd048e186199433be2c41bd3d6d \
                   "
DEPENDS = "libusb1 virtual/libiconv udev"

SRC_URI = "${KERNELORG_MIRROR}/linux/utils/usb/usbutils/usbutils-${PV}.tar.gz \
          "
SRC_URI[sha256sum] = "0048d2d8518fb0cc7c0516e16e52af023e52b55ddb3b2068a77041b5ef285768"

inherit meson pkgconfig update-alternatives

ALTERNATIVE:${PN} = "lsusb"
ALTERNATIVE_PRIORITY = "100"

# The binaries are mostly GPL-2.0-or-later apart from lsusb.py which is
# GPL-2.0-only or GPL-3.0-only.
LICENSE:${PN} = "GPL-2.0-or-later"
LICENSE:${PN}-python = "GPL-2.0-only | GPL-3.0-only"

RRECOMMENDS:${PN} = "udev-hwdb"

PACKAGE_BEFORE_PN =+ "${PN}-python"
FILES:${PN}-python += "${bindir}/lsusb.py"
RDEPENDS:${PN}-python = "python3-core"
