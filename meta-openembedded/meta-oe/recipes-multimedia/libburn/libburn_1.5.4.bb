SUMMARY = "Library for reading, mastering and writing optical discs"
HOMEPAGE = "http://libburnia-project.org/"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=9ac2e7cff1ddaf48b6eab6028f23ef88"

SRC_URI = "http://files.libburnia-project.org/releases/${BPN}-${PV}.tar.gz"
SRC_URI[md5sum] = "454d03ce31addb5b7dca62d213c9660e"
SRC_URI[sha256sum] = "525059d10759c5cb8148eebc863bb510e311c663603da7bd2d21c46b7cf63b54"

inherit autotools pkgconfig
