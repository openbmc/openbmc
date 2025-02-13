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
SRCREV = "7489280a7a1247f23543343aba4e95bf2f2e8fa6"
S = "${WORKDIR}/git"

DEPENDS = "libtinyxml2 libarchive"
inherit cmake pkgconfig

FILES:${PN} = "${bindir}"

BBCLASSEXTEND = "native nativesdk"
