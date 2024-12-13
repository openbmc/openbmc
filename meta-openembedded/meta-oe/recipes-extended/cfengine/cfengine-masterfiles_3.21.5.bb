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
LIC_FILES_CHKSUM = "file://LICENSE;md5=bb843e794feb6890f7697637b461c36e"

SRC_URI = "https://cfengine-package-repos.s3.amazonaws.com/tarballs/${BP}.tar.gz \
           "
#SRC_URI[md5sum] = "5df2f85c75efc351ffadebcc11046a98"
SRC_URI[sha256sum] = "0adcfcf36d47317776ad64331058a4d2e3669683314716ddb885f8a13309f47d"

UPSTREAM_CHECK_URI = "https://github.com/cfengine/masterfiles/releases"
UPSTREAM_CHECK_REGEX = "(?P<pver>\d+(\.\d+)+)"

inherit autotools

export EXPLICIT_VERSION="${PV}"

EXTRA_OECONF = "--prefix=${datadir}/cfengine"

do_install:append() {
    rm -rf ${D}${datadir}/cfengine/modules/packages/zypper ${D}${datadir}/cfengine/modules/packages/yum
}

FILES:${PN} = "${datadir}/cfengine"

RDEPENDS:${PN} += "python3-core"
