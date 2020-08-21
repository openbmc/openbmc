SUMMARY = "Exif, Iptc and XMP metadata manipulation library and tools"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=625f055f41728f84a8d7938acc35bdc2"

DEPENDS = "zlib expat"

SRC_URI = "https://exiv2.org/releases/${BPN}-${PV}-Source.tar.gz"
SRC_URI[sha256sum] = "a79f5613812aa21755d578a297874fb59a85101e793edc64ec2c6bd994e3e778"

# Once patch is obsolete (project should be aware due to PRs), dos2unix can be removed either
inherit dos2unix
SRC_URI += "file://0001-Use-compiler-fcf-protection-only-if-compiler-arch-su.patch"

S = "${WORKDIR}/${BPN}-${PV}-Source"

inherit cmake gettext
