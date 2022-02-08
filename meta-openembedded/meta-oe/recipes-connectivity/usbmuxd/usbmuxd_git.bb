DESCRIPTION = "This daemon is in charge of multiplexing connections over USB to an iPhone or iPod touch."
LICENSE = "GPLv3 & GPLv2 & LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING.GPLv2;md5=ebb5c50ab7cab4baeffba14977030c07 \
                    file://COPYING.GPLv3;md5=d32239bcb673463ab874e80d47fae504"

DEPENDS = "udev libusb1 libplist"

inherit autotools pkgconfig gitpkgv systemd

PKGV = "${GITPKGVTAG}"

SRCREV = "ee85938c21043ef5f7cd4dfbc7677f385814d4d8"
SRC_URI = "git://github.com/libimobiledevice/usbmuxd;protocol=https;branch=master"

S = "${WORKDIR}/git"

EXTRA_OECONF += "--without-preflight"

FILES_${PN} += "${base_libdir}/udev/rules.d/"

SYSTEMD_SERVICE_${PN} = "usbmuxd.service"
