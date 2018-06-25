DESCRIPTION = "Library for opening, seeking, and decoding opus audio files"
HOMEPAGE = "https://www.opus-codec.org/"
SECTION = "audio"

DEPENDS = "libogg openssl libopus"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=6ac22b992dde6a891f8949c3e2da8576"

SRC_URI = "https://downloads.xiph.org/releases/opus/${PN}-${PV}.tar.gz"
SRC_URI[md5sum] = "ab3f7d15d766f5b36b0951ee435f9ebf"
SRC_URI[sha256sum] = "48e03526ba87ef9cf5f1c47b5ebe3aa195bd89b912a57060c36184a6cd19412f"

inherit autotools pkgconfig
