SUMMARY = "Secure Reliable Transport (SRT) Protocol"
DESCRIPTION = "Secure Reliable Transport (SRT) is an open source transport technology \
that optimizes streaming performance across unpredictable networks, such as the Internet."
SECTION = "libs"
HOMEPAGE = "https://github.com/Haivision/srt"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=815ca599c9df247a0c7f619bab123dad"

SRCREV = "50b7af06f3a0a456c172b4cb3aceafa8a5cc0036"
SRC_URI = "git://github.com/Haivision/srt;protocol=https \
           file://0001-don-t-install-srt-ffplay.patch \
           file://0001-core-Fix-build-with-GCC-11.-1806.patch \
           "
UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>\d+(\.\d+)+)"
S = "${WORKDIR}/git"

inherit cmake pkgconfig

EXTRA_OECMAKE = "-DCMAKE_BUILD_TYPE=Release -DENABLE_UNITTESTS=OFF"

PACKAGECONFIG ??= "crypt"
PACKAGECONFIG[debug] = "-DENABLE_DEBUG=1,,"
PACKAGECONFIG[crypt] = "-DENABLE_ENCRYPTION=ON,-DENABLE_ENCRYPTION=OFF,openssl"
PACKAGECONFIG[utils] = "-DENABLE_APPS=ON,-DENABLE_APPS=OFF,"

PACKAGES += "${@bb.utils.contains('PACKAGECONFIG', 'utils', '${PN}-utils', '', d)}"
FILES_${PN}-utils += "${bindir}"
RDEPENDS_${PN}-utils += "${PN}"
