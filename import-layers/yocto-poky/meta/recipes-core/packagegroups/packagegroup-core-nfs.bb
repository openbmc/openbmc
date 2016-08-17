#
# Copyright (C) 2008 OpenedHand Ltd.
#

SUMMARY = "NFS package groups"
LICENSE = "MIT"
PR = "r2"

inherit packagegroup

PROVIDES = "${PACKAGES}"
PACKAGES = "${PN}-server ${PN}-client"

SUMMARY_${PN}-client = "NFS client"
RDEPENDS_${PN}-client = "nfs-utils-client"

SUMMARY_${PN}-server = "NFS server"
RDEPENDS_${PN}-server = "\
    nfs-utils \
    nfs-utils-client \
    "
