#
# Copyright (C) 2008 OpenedHand Ltd.
#

SUMMARY = "NFS package groups"
PR = "r2"

inherit packagegroup

PACKAGES = "${PN}-server ${PN}-client"

SUMMARY:${PN}-client = "NFS client"
RDEPENDS:${PN}-client = "nfs-utils-client"

SUMMARY:${PN}-server = "NFS server"
RDEPENDS:${PN}-server = "\
    nfs-utils \
    nfs-utils-client \
    "
