SUMMARY = "UDP broadcast file distribution and installation"
DESCRIPTION = "UDPcast is a file transfer tool that can send data \
simultaneously to many destinations on a LAN. This can for instance be \
used to install entire classrooms of PC's at once. The advantage of UDPcast \
over using other methods (nfs, ftp, whatever) is that UDPcast uses UDP's \
multicast abilities: it won't take longer to install 15 machines than it would \
to install just 2."
HOMEPAGE = "http://www.udpcast.linux.lu/"
SECTION = "console/network"
LICENSE = "GPL-2.0-or-later & BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=e3cf524a29c8476be354bb329d36ff80"

SRC_URI = "http://www.udpcast.linux.lu/download/${BP}.tar.gz \
           file://0001-include-stddef.h-for-ptrdiff_t.patch \
           "
SRC_URI[sha256sum] = "cefd7554c877e1bc76987d2b96b23f7699a2e5340c254454f61b6e0dae370aa7"

# Installation of rateGovernor.h fails without brokensep
inherit autotools-brokensep manpages

PACKAGECONFIG[manpages] = ""

# pod2man required to build manpages
DEPENDS += "perl-native"
