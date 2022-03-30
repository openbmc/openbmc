# common part for gutenprint-native / gutenprint
#
# As we are using the native part for generating string header to the target
# version, it is important they use the same version.
# So we set the SRC_URI and SRCREV here.
#
# Copyright (c) Ambu A/S - All rights reserved
# SPDX-License-Identifier: MIT
#
# Author(s)
#   clst@ambu.com (Claus Stovgaard)
#

DESCRIPTION = "Gutenprint printer drivers"
HOMEPAGE = "http://gimp-print.sourceforge.net/"
LICENSE = "GPL-2.0-or-later"

SRC_URI = "git://git.code.sf.net/p/gimp-print/source;protocol=https;branch=master"
SRCREV = "8e47dc44dd7738302ba9e8fbc1f918461fed0236"
S = "${WORKDIR}/git"

LIC_FILES_CHKSUM = "file://${S}/COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"