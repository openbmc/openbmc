DESCRIPTION = "Library for opening, seeking, and decoding opus audio files"
HOMEPAGE = "https://www.opus-codec.org/"
SECTION = "audio"

DEPENDS = "libogg openssl libopus"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=6ac22b992dde6a891f8949c3e2da8576"

SRC_URI = "https://downloads.xiph.org/releases/opus/${BP}.tar.gz"
SRC_URI[md5sum] = "45e8c62f6cd413395223c82f06bfa8ec"
SRC_URI[sha256sum] = "118d8601c12dd6a44f52423e68ca9083cc9f2bfe72da7a8c1acb22a80ae3550b"

SRC_URI += "file://CVE-2022-47021.patch"

inherit autotools pkgconfig
