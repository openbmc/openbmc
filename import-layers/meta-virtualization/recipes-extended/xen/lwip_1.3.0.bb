# Copyright (C) 2018 kebodiker <kurt.bodiker@braintrust-us.com>
# Released under the MIT license (see COPYING.MIT for the terms)

DESCRIPTION = "LWIP"
HOMEPAGE = "https://savannah.nongnu.org/projects/lwip"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=59a383b05013356e0c9899b06dc5da3f"

SRCREV_lwip = "bcb4afa886408bf0a1dde9c2a4a00323c8b07eb1"
SRC_URI = "\
    git://git.savannah.gnu.org/lwip.git;protocol=git;nobranch=1;destsuffix=lwip;name=lwip \
    file://lwip.patch-cvs \
    file://lwip.dhcp_create_request-hwaddr_len.patch \
"

S="${WORKDIR}/${PN}"
B="${S}"

require lwip.inc
