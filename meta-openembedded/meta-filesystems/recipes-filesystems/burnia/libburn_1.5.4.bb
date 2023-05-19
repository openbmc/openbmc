SUMMARY = "library to write an ISO-9660 file system to physical media"
HOMEPAGE = "https://libburnia-project.org/"

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=9ac2e7cff1ddaf48b6eab6028f23ef88 \
                    file://COPYRIGHT;md5=a91b9b862895b64e68e5b321873c9111"

SRC_URI = "http://files.libburnia-project.org/releases/${BP}.tar.gz"
SRC_URI[sha256sum] = "525059d10759c5cb8148eebc863bb510e311c663603da7bd2d21c46b7cf63b54"

inherit autotools pkgconfig lib_package

BBCLASSEXTEND = "native"
