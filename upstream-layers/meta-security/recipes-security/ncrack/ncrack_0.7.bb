SUMMARY = "Network authentication cracking tool"
DESCRIPTION = "Ncrack is designed for high-speed parallel testing of network devices for poor passwords."
HOMEPAGE = "https://nmap.org/ncrack"
SECTION = "security"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;beginline=7;endline=12;md5=8df67d60bde9d6c7b7030546ab47bf4b"

DEPENDS = "openssl zlib"

SRC_URI = "git://github.com/nmap/ncrack.git;branch=master;protocol=https"
SRCREV = "7fab46addcb99326cbf60f41dbde22a1e87aebad"

PV = "0.7+git"

inherit autotools-brokensep

INSANE_SKIP:${PN} = "already-stripped"
