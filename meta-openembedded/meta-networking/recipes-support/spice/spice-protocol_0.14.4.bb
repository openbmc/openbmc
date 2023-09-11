#
# Copyright (C) 2013 Wind River Systems, Inc.
#

SUMMARY = "Simple Protocol for Independent Computing Environments (protocol definition"
HOMEPAGE = "https://spice-space.org"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=b37311cb5604f3e5cc2fb0fd23527e95"

SRCREV = "6f453a775d87087c6ba59fc180c1a1e466631a47"

SRC_URI = "git://gitlab.freedesktop.org/spice/spice-protocol.git;protocol=https;branch=master"

S = "${WORKDIR}/git"

inherit meson pkgconfig

ALLOW_EMPTY:${PN} = "1"

BBCLASSEXTEND = "native nativesdk"
