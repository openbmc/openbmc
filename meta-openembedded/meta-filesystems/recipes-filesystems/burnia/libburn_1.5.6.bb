SUMMARY = "library to write an ISO-9660 file system to physical media"
HOMEPAGE = "https://libburnia-project.org/"

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=9ac2e7cff1ddaf48b6eab6028f23ef88 \
                    file://COPYRIGHT;md5=a91b9b862895b64e68e5b321873c9111"

SRC_URI = "http://files.libburnia-project.org/releases/${BP}.tar.gz"
SRC_URI[sha256sum] = "7295491b4be5eeac5e7a3fb2067e236e2955ffdc6bbd45f546466edee321644b"

inherit autotools pkgconfig lib_package

BBCLASSEXTEND = "native"
