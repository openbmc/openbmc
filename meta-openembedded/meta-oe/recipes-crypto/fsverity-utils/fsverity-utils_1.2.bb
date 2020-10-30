SUMMARY = "Userspace utilities for fs-verity"
DESCRIPTION = "fs-verity is a Linux kernel feature that does transparent \
on-demand integrity/authenticity verification of the contents of read-only \
files, using a hidden Merkle tree (hash tree) associated with the file. The \
mechanism is similar to dm-verity, but implemented at the file level rather \
than at the block device level."
HOMEPAGE = "https://www.kernel.org/doc/html/latest/filesystems/fsverity.html"
SECTION = "console"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=bc974d217b525ea216a336adb73e1220"

SRCREV = "406187bb3a885e083b448b2c2aa42c22957ae629"
SRC_URI = "git://git.kernel.org/pub/scm/linux/kernel/git/ebiggers/fsverity-utils.git"

S = "${WORKDIR}/git"

DEPENDS = "openssl"

EXTRA_OEMAKE += "PREFIX=${prefix} USE_SHARED_LIB=1"

do_install() {
        oe_runmake install DESTDIR=${D}
}

PACKAGES =+ "libfsverity"
FILES_libfsverity = "${libdir}/libfsverity*${SOLIBS}"

BBCLASSEXTEND = "native nativesdk"
