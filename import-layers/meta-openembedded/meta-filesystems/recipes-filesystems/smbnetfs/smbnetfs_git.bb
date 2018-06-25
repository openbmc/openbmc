SUMMARY = "FUSE module for mounting an entire SMB/NMB network in a single directory"
DESCRIPTION = "SMBNetFS is a Linux/FreeBSD filesystem that allow you to use \
               samba/microsoft network in the same manner as the network \
               neighborhood in Microsoft Windows. Please donate me to help \
               in SMBNetFS development."

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=eb723b61539feef013de476e68b5c50a"
HOMEPAGE ="http://sourceforge.net/projects/smbnetfs"

DEPENDS = "fuse samba"
DEPENDS_append_libc-musl = " libexecinfo"

# samba depends on libpam
inherit distro_features_check
REQUIRED_DISTRO_FEATURES = "pam"

inherit autotools gitpkgv pkgconfig

PKGV = "${GITPKGVTAG}"

SRCREV = "bc6b94b015fdaf7c4dab56ccb996eecea8bc4373"

SRC_URI = "git://smbnetfs.git.sourceforge.net/gitroot/smbnetfs/smbnetfs;branch=master \
           file://configure.patch \
           file://Using-PKG_CHECK_MODULES-to-found-headers-and-libraries.patch"

PACKAGECONFIG ??= ""
PACKAGECONFIG[libsecret] = "--with-libsecret=yes,--with-libsecret=no,libsecret"

S = "${WORKDIR}/git"

LDFLAGS_append_libc-musl = " -lexecinfo"
