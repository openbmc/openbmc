SUMMARY = "Library for reading, mastering and writing optical discs"
HOMEPAGE = "http://libburnia-project.org/"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=9ac2e7cff1ddaf48b6eab6028f23ef88"

SRC_URI = "http://files.libburnia-project.org/releases/${BPN}-${PV}.tar.gz"
SRC_URI[md5sum] = "096f4acfba00f1210a84fb7650f7693d"
SRC_URI[sha256sum] = "7b32db1719d7f6516cce82a9d00dfddfb3581725db732ea87d41ea8ef0ce5227"

inherit autotools pkgconfig
