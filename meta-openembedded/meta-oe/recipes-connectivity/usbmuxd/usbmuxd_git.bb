DESCRIPTION = "This daemon is in charge of multiplexing connections over USB to an iPhone or iPod touch."
LICENSE = "GPLv3 & GPLv2 & LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING.GPLv2;md5=ebb5c50ab7cab4baeffba14977030c07 \
                    file://COPYING.GPLv3;md5=d32239bcb673463ab874e80d47fae504 \
                    file://COPYING.LGPLv2.1;md5=6ab17b41640564434dda85c06b7124f7"

DEPENDS = "udev libusb1"

inherit cmake pkgconfig gitpkgv

PKGV = "${GITPKGVTAG}"

SRCREV = "919587580c5e77f3936f3432115d2e10c7bac7c5"
SRC_URI = "git://git.sukimashita.com/usbmuxd.git;protocol=http"

S = "${WORKDIR}/git"

FILES_${PN} += "${base_libdir}/udev/rules.d/"

# fix usbmuxd installing files to /usr/lib64 on 64bit hosts:
EXTRA_OECMAKE = "-DLIB_SUFFIX=${@d.getVar('baselib').replace('lib', '')}"

PACKAGECONFIG ??= ""
PACKAGECONFIG[plist] = "-DWANT_PLIST=1,-DWANT_PLIST=0,libplist"
