SUMMARY = "FUSE module for mounting an entire SMB/NMB network in a single directory"
DESCRIPTION = "SMBNetFS is a Linux/FreeBSD filesystem that allow you to use \
               samba/microsoft network in the same manner as the network \
               neighborhood in Microsoft Windows. Please donate me to help \
               in SMBNetFS development."

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=eb723b61539feef013de476e68b5c50a"
HOMEPAGE ="http://sourceforge.net/projects/smbnetfs"

DEPENDS = "fuse samba"

inherit autotools gitpkgv pkgconfig

PKGV = "${GITPKGVTAG}"

SRCREV = "ace1c519d45fe488b9b7e6cc77a2bcadb6c83464"

SRC_URI = "git://smbnetfs.git.sourceforge.net/gitroot/smbnetfs/smbnetfs;branch=master \
           file://configure.patch \
           file://Using-PKG_CHECK_MODULES-to-found-headers-and-libraries.patch"

PACKAGECONFIG ??= ""
PACKAGECONFIG[gnome-keyring] = "--with-gnome-keyring=yes,--with-gnome-keyring=no,libgnome-keyring"

S = "${WORKDIR}/git"
