SUMMARY = "bmaptool alternative written in C++"
DESCRIPTION = "bmap-writer is a command-line utility designed to efficiently write disk images \
to storage devices using block mapping (BMAP). It serves as \
a lightweight alternative to the Yocto BMAP tool, \
bmap-writer is C++ based does not require Python and focuses solely on writing an image."
HOMEPAGE = "https://github.com/embetrix/bmap-writer"
SECTION = "console/utils"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e49f4652534af377a713df3d9dec60cb"

SRC_URI = "git://github.com/embetrix/${BPN};branch=master;protocol=https"
SRCREV = "74826a19f7eb31e029405cd0ea6b07c2aaddc73d"
S = "${WORKDIR}/git"

DEPENDS= "openssl libxml2 xz zlib"
inherit cmake

EXTRA_OEMAKE += 'LIBXML2_HEADER_PATH="${STAGING_INCDIR}/libxml2"'

FILES:${PN} = "${bindir}/bmap-writer"

BBCLASSEXTEND = "native nativesdk"
