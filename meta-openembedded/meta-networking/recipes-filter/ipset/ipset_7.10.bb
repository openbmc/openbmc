# Copyright (C) 2017 Aaron Brice <aaron.brice@datasoft.com>
# Released under the MIT license (see COPYING.MIT for the terms)

DESCRIPTION = "Administration tool for IP sets"
HOMEPAGE = "http://ipset.netfilter.org"
LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"
SECTION = "base"

DEPENDS = "libtool libmnl"

SRC_URI = "http://ftp.netfilter.org/pub/ipset/${BP}.tar.bz2"
SRC_URI[sha256sum] = "b24906ba43e2fe322bd41863476761d749a4bddf5ce4c2265ba04b03bc7e9cf6"

inherit autotools pkgconfig module-base

EXTRA_OECONF += "-with-kbuild=${KBUILD_OUTPUT} --with-ksource=${STAGING_KERNEL_DIR}"

RRECOMMENDS_${PN} = "\
    kernel-module-ip-set \
"
