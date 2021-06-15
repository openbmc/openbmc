#
# Copyright (C) 2014 - 2017 Wind River Systems, Inc.
#
SUMMARY = "Base policy for CFEngine"

DESCRIPTION = "CFEngine is an IT infrastructure automation framework \
that helps engineers, system administrators and other stakeholders \
in an IT system to manage and understand IT infrastructure throughout \
its lifecycle. CFEngine takes systems from Build to Deploy, Manage and Audit. \
 \
This package is intended to provide a stable base policy for \
installations and upgrades, and is used by CFEngine 3.6 and newer. \
 \
The contents of this packge are intended to live in `/var/cfengine/masterfiles` \
or wherever `$(sys.masterdir)` points. \
"

HOMEPAGE = "http://cfengine.com"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9f76426f9ef8c6f6739fadd21d817a4f"

SRC_URI = "https://cfengine-package-repos.s3.amazonaws.com/tarballs/${BP}.tar.gz \
           file://python3.patch \
           "
SRC_URI[md5sum] = "6d456fdd9bd24ff6617eeaa05efae602"
SRC_URI[sha256sum] = "4a071c0c4ba7df9bad93144cff5fbc0566e5172afd66201072e3193b76c55a38"

inherit autotools

export EXPLICIT_VERSION="${PV}"

EXTRA_OECONF = "--prefix=${datadir}/cfengine"

do_install_append() {
    rm -rf ${D}${datadir}/cfengine/modules/packages/zypper ${D}${datadir}/cfengine/modules/packages/yum
}

FILES_${PN} = "${datadir}/cfengine"

RDEPENDS_${PN} += "python3-core"
