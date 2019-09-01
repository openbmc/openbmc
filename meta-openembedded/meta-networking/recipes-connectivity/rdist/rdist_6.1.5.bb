SUMMARY = "Remote file distribution client and server"
DESCRIPTION = "\
Rdist is a program to maintain identical copies of files over multiple \
hosts. It preserves the owner, group, mode, and mtime of files if \
possible and can update programs that are executing. \
"
SECTION = "console/network"
LICENSE = "BSD-4-Clause"
LIC_FILES_CHKSUM = "file://Copyright;md5=3f47ec9f64b11c8192ee05a66b5c2755"

SRC_URI = "http://www.magnicomp.com/download/${BPN}/${BP}.tar.gz"
SRC_URI[md5sum] = "546779700af70aa5f9103e08782cdcac"
SRC_URI[sha256sum] = "2bb0d0f5904eadc9e7fe3d60c15389d6897fcf884211070e289a6c710ff37f96"

SRC_URI += "file://rdist-6.1.5-linux.patch \
            file://rdist-6.1.5-links.patch \
            file://rdist-6.1.5-oldpath.patch \
            file://rdist-6.1.5-hardlink.patch \
            file://rdist-6.1.5-bison.patch \
            file://rdist-6.1.5-varargs.patch \
            file://rdist-6.1.5-maxargs.patch \
            file://rdist-6.1.5-lfs.patch \
            file://rdist-6.1.5-cleanup.patch \
            file://rdist-6.1.5-svr4.patch \
            file://rdist-6.1.5-ssh.patch \
            file://rdist-6.1.5-mkstemp.patch \
            file://rdist-6.1.5-stat64.patch \
            file://rdist-6.1.5-fix-msgsndnotify-loop.patch \
            file://rdist-6.1.5-bb-build.patch \
            file://rdist-6.1.5-makefile-add-ldflags.patch \
"

UPSTREAM_CHECK_URI = "https://sourceforge.net/projects/rdist/files/rdist/"
UPSTREAM_CHECK_REGEX = "/rdist/(?P<pver>\d+(\.\d+)+)"

DEPENDS = "bison-native"

inherit autotools-brokensep

EXTRA_OEMAKE = "BIN_GROUP=root MAN_GROUP=root RDIST_MODE=755 RDISTD_MODE=755 MAN_MODE=644"

# http://errors.yoctoproject.org/Errors/Details/186972/
COMPATIBLE_HOST_libc-musl = 'null'
