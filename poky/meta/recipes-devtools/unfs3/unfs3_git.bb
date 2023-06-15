SUMMARY = "Userspace NFS server v3 protocol"
DESCRIPTION = "UNFS3 is a user-space implementation of the NFSv3 server \
specification. It provides a daemon for the MOUNT and NFS protocols, which \
are used by NFS clients for accessing files on the server."
HOMEPAGE = "https://github.com/unfs3/unfs3"
SECTION = "console/network"
LICENSE = "unfs3"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9475885294e17c0cc0067820d042792e"

DEPENDS = "flex-native bison-native flex"
DEPENDS += "libtirpc"
DEPENDS:append:class-nativesdk = " flex-nativesdk"

S = "${WORKDIR}/git"
SRC_URI = "git://github.com/unfs3/unfs3.git;protocol=https;branch=master \
           file://0001-daemon.c-Fix-race-window-for-writing-of-the-pid-file.patch \
           file://0001-Alias-off64_t-to-off_t-on-linux-if-not-defined.patch \
           file://0001-locate.c-Include-attr.h.patch \
           file://0001-fix-building-on-macOS.patch \
           file://0001-attr-fix-utime-for-symlink.patch \
           "
SRCREV = "c8f2d2cd4529955419bad0e163f88d47ff176b8d"
UPSTREAM_CHECK_GITTAGREGEX = "unfs3\-(?P<pver>\d+(\.\d+)+)"

PV = "0.10.0"

BBCLASSEXTEND = "native nativesdk"

inherit autotools pkgconfig
EXTRA_OECONF:append:class-native = " --sbindir=${bindir}"
