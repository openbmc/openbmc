SUMMARY = "Network authentication cracking tool"
DESCRIPTION = "Ncrack is designed for high-speed parallel testing of network devices for poor passwords."
HOMEPAGE = "https://nmap.org/ncrack"
SECTION = "security"

LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=198fa93d4e80225839e595336f3b5ff0"

SRCREV = "3a793a21820708466081825beda9fce857f36cb6"
SRC_URI = "git://github.com/nmap/ncrack.git"

DEPENDS = "openssl zlib"

inherit autotools-brokensep

S = "${WORKDIR}/git"

INSANE_SKIP_${PN} = "already-stripped"
