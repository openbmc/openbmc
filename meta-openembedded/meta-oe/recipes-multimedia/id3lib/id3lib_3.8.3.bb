SUMMARY = "Library for interacting with ID3 tags"
SECTION = "libs/multimedia"
LICENSE = "LGPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=3bf50002aefd002f49e7bb854063f7e7"
DEPENDS = "zlib"

PR = "r1"

SRC_URI = "${SOURCEFORGE_MIRROR}/id3lib/id3lib-${PV}.tar.gz;name=archive \
           http://ftp.de.debian.org/debian/pool/main/i/id3lib3.8.3/id3lib3.8.3_3.8.3-7.2.diff.gz;name=patch \
           file://acdefine.patch \
"
SRC_URI[archive.md5sum] = "19f27ddd2dda4b2d26a559a4f0f402a7"
SRC_URI[archive.sha256sum] = "2749cc3c0cd7280b299518b1ddf5a5bcfe2d1100614519b68702230e26c7d079"
SRC_URI[patch.md5sum] = "805c0320a2efb21c40ce06fa13cd7c4b"
SRC_URI[patch.sha256sum] = "9f03b59ccc8826a5be55a3dcde2f889067d58bdc72bf846416a198c9b933704c"

inherit autotools
