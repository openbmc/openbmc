# Copyright (C) 2017 Kurt Bodiker <kurt.bodiker@braintrust-us.com>
# Released under the MIT license (see COPYING.MIT for the terms)

DESCRIPTION = "Mini-OS is a tiny OS kernel distributed with the Xen Project"
HOMEPAGE = "https://wiki.xenproject.org/wiki/Mini-OS"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=8a437231894440a8f7629caa372243d0"

# git commit hash for tags: xen-RELEASE-4.9.0, xen-RELEASE-4.9.1, xen-RELEASE-4.9.2
SRCREV_minios = "ca013fa9baf92f47469ba1f2e1aaa31c41d8a0bb"
SRC_URI = "\
    git://xenbits.xen.org/mini-os.git;protocol=git;nobranch=1;destsuffix=mini-os;name=minios \
    file://mini-os_udivmoddi4-gcc7.patch \
"
S="${WORKDIR}/mini-os"
B="${S}"

require mini-os.inc
