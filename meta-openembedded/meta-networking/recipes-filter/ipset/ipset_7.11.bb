# Copyright (C) 2017 Aaron Brice <aaron.brice@datasoft.com>
# Released under the MIT license (see COPYING.MIT for the terms)

DESCRIPTION = "Administration tool for IP sets"
HOMEPAGE = "http://ipset.netfilter.org"
LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"
SECTION = "base"

DEPENDS = "libtool libmnl"

SRC_URI = "http://ftp.netfilter.org/pub/ipset/${BP}.tar.bz2"
SRC_URI[sha256sum] = "3151baad30f1d9e317b2ab4f2f5aa7a9f7b4dc11fcf8fe73acd0dc0b5dbabf7d"

inherit autotools pkgconfig module-base

EXTRA_OECONF += "-with-kbuild=${KBUILD_OUTPUT} --with-ksource=${STAGING_KERNEL_DIR}"

RRECOMMENDS:${PN} = "\
    kernel-module-ip-set \
"
