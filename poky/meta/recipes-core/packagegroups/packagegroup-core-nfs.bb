#
# Copyright (C) 2008 OpenedHand Ltd.
#

SUMMARY = "NFS package groups"
PR = "r2"

inherit packagegroup

PACKAGES = "${PN}-server ${PN}-client"

SUMMARY_${PN}-client = "NFS client"
RDEPENDS_${PN}-client = "nfs-utils-client"

SUMMARY_${PN}-server = "NFS server"
RDEPENDS_${PN}-server = "\
    nfs-utils \
    nfs-utils-client \
    "
