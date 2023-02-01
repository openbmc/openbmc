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

SRCREV = "20e87c13075a8e5660a8d69fd6c93d4f7c5f01a5"
SRC_URI = "git://git.kernel.org/pub/scm/linux/kernel/git/ebiggers/fsverity-utils.git;branch=master"

S = "${WORKDIR}/git"

DEPENDS = "openssl"

EXTRA_OEMAKE:append = " PREFIX=${prefix} LIBDIR=${libdir} USE_SHARED_LIB=1"
# We want to statically link the binary to libfsverity on native Windows
EXTRA_OEMAKE:remove:mingw32:class-nativesdk = "USE_SHARED_LIB=1"
EXTRA_OEMAKE:remove:mingw32:class-native = "USE_SHARED_LIB=1"

do_install() {
        oe_runmake install DESTDIR=${D}
}

PACKAGES =+ "libfsverity"
FILES:libfsverity = "${libdir}/libfsverity*${SOLIBS}"

BBCLASSEXTEND = "native nativesdk"
