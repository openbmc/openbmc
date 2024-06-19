SUMMARY = "Tools to handle creating and mounting composefs images"
DESCRIPTION = "The composefs project combines several underlying Linux \
features to provide a very flexible mechanism to support read-only mountable \
filesystem trees, stacking on top of an underlying "lower" Linux filesystem."
HOMEPAGE = "https://github.com/containers/composefs"
LICENSE = "GPL-3.0-or-later & LGPL-2.0-or-later & Apache-2.0"
LIC_FILES_CHKSUM = "\
    file://BSD-2-Clause.txt;md5=121c8a0a8fa5961a26b7863034ebcce8 \
    file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
    file://COPYING.LESSERv3;md5=6a6a8e020838b23406c81b19c1d46df6 \
    file://COPYING.LIB;md5=4fbd65380cdd255951079008b364516c \
    file://COPYINGv3;md5=d32239bcb673463ab874e80d47fae504 \
    file://LICENSE.Apache-2.0;md5=3b83ef96387f14655fc854ddc3c6bd57 \
"

PV .= "+git${SRCPV}"
SRCREV = "7623e4dc89f62ada5724d4e41d0a16d2671312f5"
SRC_URI = "git://github.com/containers/composefs.git;protocol=https;branch=main"

S = "${WORKDIR}/git"

inherit autotools pkgconfig

DEPENDS = "openssl"

EXTRA_OECONF += " \
    --disable-man \
    --without-fuse \
"

LDFLAGS:append:class-native = " -pthread"

BBCLASSEXTEND = "native"
