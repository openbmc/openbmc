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

SRC_URI = "https://cfengine-package-repos.s3.amazonaws.com/tarballs/${BP}.tar.gz"

SRC_URI[md5sum] = "d59bd8c95620da816c382f749d425860"
SRC_URI[sha256sum] = "1c50e3d8c702097e13a21258626d936d6ff2e6492e893dfe286ff0d6204d7a65"

inherit autotools

export EXPLICIT_VERSION="${PV}"

EXTRA_OECONF = "--prefix=${datadir}/cfengine"

FILES_${PN} = "${datadir}/cfengine"

RDEPENDS_${PN} += "python-core"
