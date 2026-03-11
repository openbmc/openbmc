SUMMARY = "Userspace NFS server v3 protocol"
DESCRIPTION = "UNFS3 is a user-space implementation of the NFSv3 server \
specification. It provides a daemon for the MOUNT and NFS protocols, which \
are used by NFS clients for accessing files on the server."
HOMEPAGE = "https://github.com/unfs3/unfs3"
SECTION = "console/network"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c1c621cd2786a3a1344a60a0d608c910"

DEPENDS = "bison-native flex-native libtirpc"

SRC_URI = "git://github.com/unfs3/unfs3.git;protocol=https;branch=master;tag=${BP}"
SRCREV = "ec1660ba33c80d5c67131e163e68834c1a10e243"
UPSTREAM_CHECK_GITTAGREGEX = "unfs3\-(?P<pver>\d+(\.\d+)+)"

inherit autotools pkgconfig

EXTRA_OECONF:append:class-native = " --sbindir=${bindir}"

BBCLASSEXTEND = "native nativesdk"
