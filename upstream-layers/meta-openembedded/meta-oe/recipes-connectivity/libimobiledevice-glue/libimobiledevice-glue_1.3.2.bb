SUMMARY = "Library with common code used by the libraries and tools around the libimobiledevice project"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "\
    file://COPYING;md5=6ab17b41640564434dda85c06b7124f7 \
"

HOMEPAGE = "http://www.libimobiledevice.org/"

DEPENDS = "libplist"

SRCREV = "aef2bf0f5bfe961ad83d224166462d87b1df2b00"
SRC_URI = "git://github.com/libimobiledevice/libimobiledevice-glue;protocol=https;branch=master"

inherit autotools pkgconfig
