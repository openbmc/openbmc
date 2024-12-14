SUMMARY = "The libirecovery library allows communication with iBoot/iBSS of iOS devices via USB"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "\
    file://COPYING;md5=23c2a5e0106b99d75238986559bb5fc6 \
"

HOMEPAGE = "http://www.libimobiledevice.org/"

DEPENDS = "libimobiledevice-glue libusb1 readline"

SRCREV = "2fb767d784c01269a0ded5bacd5539aee3768c35"
SRC_URI = "git://github.com/libimobiledevice/libirecovery;protocol=https;branch=master"

S = "${WORKDIR}/git"
inherit autotools pkgconfig
