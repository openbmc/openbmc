SUMARRY=" Intelligently block brute-force attacks by aggregating system logs "
HOMEPAGE = "https://www.sshguard.net/"
LIC_FILES_CHKSUM = "file://COPYING;md5=47a33fc98cd20713882c4d822a57bf4d"
LICENSE = "BSD-1-Clause"


SRC_URI="https://sourceforge.net/projects/sshguard/files/sshguard/${PV}/sshguard-${PV}.tar.gz"

SRC_URI[sha256sum] = "64029deff6de90fdeefb1f497d414f0e4045076693a91da1a70eb7595e97efeb"

inherit autotools-brokensep
