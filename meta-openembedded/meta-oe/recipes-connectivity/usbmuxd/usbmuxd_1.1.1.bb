DESCRIPTION = "This daemon is in charge of multiplexing connections over USB to an iPhone or iPod touch."
HOMEPAGE = "https://github.com/libimobiledevice/usbmuxd"
LICENSE = "GPLv3 & GPLv2 & LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING.GPLv2;md5=ebb5c50ab7cab4baeffba14977030c07 \
                    file://COPYING.GPLv3;md5=d32239bcb673463ab874e80d47fae504"

DEPENDS = "udev libusb1 libplist"

inherit autotools pkgconfig gitpkgv systemd

PKGV = "${GITPKGVTAG}"

SRCREV = "79c8b38d1488a6b07e1e68f39d8caec3f1a45622"
SRC_URI = "git://github.com/libimobiledevice/usbmuxd;protocol=https"

S = "${WORKDIR}/git"

EXTRA_OECONF += "--without-preflight"

FILES_${PN} += "${base_libdir}/udev/rules.d/"

SYSTEMD_SERVICE_${PN} = "usbmuxd.service"
