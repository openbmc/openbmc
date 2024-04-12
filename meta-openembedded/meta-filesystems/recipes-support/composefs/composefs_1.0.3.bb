SUMMARY = "Tools to handle creating and mounting composefs images"
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
DEPENDS = "fuse3 openssl"
SRCREV = "2d5cdcb9176cfe4ccf1761ef6d78e1c48de35649"
PV = "1.0.3"

SRC_URI = "\
    git://github.com/containers/composefs.git;protocol=https;branch=main \
    file://0001-musl-basename-use-portable-implementation-for-basena.patch \
"

S = "${WORKDIR}/git"

inherit autotools pkgconfig
