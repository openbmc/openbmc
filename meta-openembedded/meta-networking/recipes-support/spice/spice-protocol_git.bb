#
# Copyright (C) 2013 Wind River Systems, Inc.
#

SUMMARY = "Simple Protocol for Independent Computing Environments"
DESCRIPTION = "SPICE (the Simple Protocol for Independent Computing \
Environments) is a remote-display system built for virtual \
environments which allows users to view a computing 'desktop' \ 
environment - not only on its computer-server machine, but also from \
anywhere on the Internet and using a wide variety of machine \
architectures."

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=b37311cb5604f3e5cc2fb0fd23527e95"

PV = "0.12.13+git${SRCPV}"

SRCREV = "87441524f4e7b79658e42bd8f1f6c3e3c8649aa5"

SRC_URI = " \
    git://anongit.freedesktop.org/spice/spice-protocol \
"

S = "${WORKDIR}/git"

inherit autotools gettext pkgconfig

BBCLASSEXTEND = "native nativesdk"
