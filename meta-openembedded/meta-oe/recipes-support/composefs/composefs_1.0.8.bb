SUMMARY = "Tools to handle creating and mounting composefs images"
DESCRIPTION = "The composefs project combines several underlying Linux \
features to provide a very flexible mechanism to support read-only mountable \
filesystem trees, stacking on top of an underlying "lower" Linux filesystem."
HOMEPAGE = "https://github.com/containers/composefs"
LICENSE = "GPL-2.0-only & GPL-2.0-or-later & LGPL-2.1-or-later & Apache-2.0"
LIC_FILES_CHKSUM = "\
    file://BSD-2-Clause.txt;md5=121c8a0a8fa5961a26b7863034ebcce8 \
    file://COPYING;md5=5cbca48090f7fe0169186a551a5bf78c \
    file://COPYING.GPL-2.0-only;md5=94fa01670a2a8f2d3ab2de15004e0848 \
    file://COPYING.GPL-2.0-or-later;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
    file://COPYING.LGPL-2.1-or-later;md5=4fbd65380cdd255951079008b364516c \
    file://LICENSE.Apache-2.0;md5=3b83ef96387f14655fc854ddc3c6bd57 \
"

SRCREV = "858ce1b38e1534c2602eb431124b5dca706bc746"
SRC_URI = "git://github.com/containers/composefs.git;protocol=https;branch=main"

S = "${WORKDIR}/git"

inherit meson

DEPENDS = "openssl"

EXTRA_OEMESON += " \
    -Dman=disabled \
    -Dfuse=disabled \
"

BBCLASSEXTEND = "native"
