SUMMARY = "library to create an ISO-9660 filesystem"
HOMEPAGE = "https://libburnia-project.org/"

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=9ac2e7cff1ddaf48b6eab6028f23ef88 \
                    file://COPYRIGHT;md5=1c1e5a960562d65f6449bb1d21e1dffc"

SRC_URI = "http://files.libburnia-project.org/releases/${BP}.tar.gz"
SRC_URI[sha256sum] = "aaa0ed80a7501979316f505b0b017f29cba0ea5463b751143bad2c360215a88e"

DEPENDS = "acl zlib"

inherit autotools

BBCLASSEXTEND = "native"
