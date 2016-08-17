SUMMARY = "Library for reading, mastering and writing optical discs"
HOMEPAGE = "http://libburnia-project.org/"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=9ac2e7cff1ddaf48b6eab6028f23ef88"

SRC_URI = "http://files.libburnia-project.org/releases/${BPN}-${PV}.tar.gz"
SRC_URI[md5sum] = "82ff94bb04e78eac9b12c7546f005d6f"
SRC_URI[sha256sum] = "6c975abae4ae1f80e47fc5d1e235f85157f73e954c84627a5ef85d8b1b95ae94"

inherit autotools pkgconfig
