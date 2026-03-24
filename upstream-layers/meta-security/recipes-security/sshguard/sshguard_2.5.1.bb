SUMMARY = " Intelligently block brute-force attacks by aggregating system logs "
HOMEPAGE = "https://www.sshguard.net/"
LIC_FILES_CHKSUM = "file://COPYING;md5=47a33fc98cd20713882c4d822a57bf4d"
LICENSE = "BSD-1-Clause"


SRC_URI = "https://sourceforge.net/projects/sshguard/files/sshguard/${PV}/sshguard-${PV}.tar.gz"

SRC_URI[sha256sum] = "997a1e0ec2b2165b4757c42f8948162eb534183946af52efc406885d97cb89fc"

inherit autotools-brokensep
