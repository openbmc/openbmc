# Copyright (C) 2017 Aaron Brice <aaron.brice@datasoft.com>
# Released under the MIT license (see COPYING.MIT for the terms)

DESCRIPTION = "Administration tool for IP sets"
HOMEPAGE = "http://ipset.netfilter.org"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"
SECTION = "base"

DEPENDS = "libtool libmnl"

SRC_URI = "https://ipset.netfilter.org/${BP}.tar.bz2 \
           file://0001-ipset-Define-portable-basename-function.patch"
SRC_URI[sha256sum] = "fbe3424dff222c1cb5e5c34d38b64524b2217ce80226c14fdcbb13b29ea36112"

UPSTREAM_CHECK_URI = "https://ipset.netfilter.org/install.html"
UPSTREAM_CHECK_REGEX = "ipset-(?P<pver>\d+(\.\d+)+).tar"

inherit autotools pkgconfig module-base

EXTRA_OECONF += "-with-kbuild=${KBUILD_OUTPUT} --with-ksource=${STAGING_KERNEL_DIR}"

RRECOMMENDS:${PN} = "\
    kernel-module-ip-set \
"
