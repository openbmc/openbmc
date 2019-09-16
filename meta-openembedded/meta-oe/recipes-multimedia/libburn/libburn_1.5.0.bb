SUMMARY = "Library for reading, mastering and writing optical discs"
HOMEPAGE = "http://libburnia-project.org/"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=9ac2e7cff1ddaf48b6eab6028f23ef88"

SRC_URI = "http://files.libburnia-project.org/releases/${BPN}-${PV}.tar.gz"
SRC_URI[md5sum] = "b32edefdd9a978edc65aacddfe7c588a"
SRC_URI[sha256sum] = "582b12c236c1365211946f2fe3c254976af37bbec244051f7742a98ded9be2bd"

inherit autotools pkgconfig
