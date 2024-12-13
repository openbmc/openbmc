SUMMARY = "Network authentication cracking tool"
DESCRIPTION = "Ncrack is designed for high-speed parallel testing of network devices for poor passwords."
HOMEPAGE = "https://nmap.org/ncrack"
SECTION = "security"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;beginline=7;endline=12;md5=66938a7e5b4c118eda78271de14874c2"

SRCREV = "dc570e7e3cec1fb176c0168eaedc723084bd0426"
SRC_URI = "git://github.com/nmap/ncrack.git;branch=master;protocol=https"

DEPENDS = "openssl zlib"

inherit autotools-brokensep

S = "${UNPACKDIR}/git"

INSANE_SKIP:${PN} = "already-stripped"
