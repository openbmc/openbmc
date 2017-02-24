#
# Copyright (C) 2014 - 2016 Wind River Systems, Inc.
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
LIC_FILES_CHKSUM = "file://LICENSE;md5=52cd3d13af93180822888ab0088e9328"

SRC_URI = "https://cfengine-package-repos.s3.amazonaws.com/tarballs/${BP}.tar.gz \
           file://remove-policy-of-usr-local-sbin.patch \
"
SRC_URI[md5sum] = "b101ddcd546738af6ec91be5c297cb24"
SRC_URI[sha256sum] = "63dec2f8649f5f2788cd463dccf47f8dbe941522acfcf3093517f983bbfa0606"

inherit autotools

export EXPLICIT_VERSION="${PV}"

EXTRA_OECONF = "--prefix=${localstatedir}/cfengine"

FILES_${PN} += "${localstatedir}/cfengine"

RDEPENDS_${PN} += "python-core"
