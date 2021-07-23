SUMARRY=" Intelligently block brute-force attacks by aggregating system logs "
HOMEPAGE = "https://www.sshguard.net/"
LIC_FILES_CHKSUM = "file://COPYING;md5=47a33fc98cd20713882c4d822a57bf4d"
LICENSE = "BSD-1-Clause"


SRC_URI="https://sourceforge.net/projects/sshguard/files/sshguard/${PV}/sshguard-${PV}.tar.gz"

SRC_URI[sha256sum] = "2770b776e5ea70a9bedfec4fd84d57400afa927f0f7522870d2dcbbe1ace37e8"

inherit autotools-brokensep
