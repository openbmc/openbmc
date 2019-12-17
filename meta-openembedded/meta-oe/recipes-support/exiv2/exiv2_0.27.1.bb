SUMMARY = "Exif, Iptc and XMP metadata manipulation library and tools"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=625f055f41728f84a8d7938acc35bdc2"

DEPENDS = "zlib expat"

SRC_URI = "https://exiv2.org/releases/${BPN}-${PV}-Source.tar.gz"
SRC_URI[md5sum] = "56d064517ae5903dd963b84514a121c1"
SRC_URI[sha256sum] = "f125286980fd1bcb28e188c02a93946951c61e10784720be2301b661a65b3081"

S = "${WORKDIR}/${BPN}-${PV}-Source"

inherit cmake gettext
