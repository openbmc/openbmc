SUMMARY = "Checksumming Copy on Write Filesystem utilities"
DESCRIPTION = "Btrfs is a new copy on write filesystem for Linux aimed at \
implementing advanced features while focusing on fault tolerance, repair and \
easy administration. \
This package contains utilities (mkfs, fsck, btrfsctl) used to work with \
btrfs and an utility (btrfs-convert) to make a btrfs filesystem from an ext3."

HOMEPAGE = "https://btrfs.wiki.kernel.org"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=fcb02dc552a041dee27e4b85c7396067"
SECTION = "base"
DEPENDS = "util-linux attr e2fsprogs lzo acl"
DEPENDS_append_class-target = " udev"
RDEPENDS_${PN} = "libgcc"

SRCREV = "a7a1ea0f4f2a1d6eeeb3d106e062c7f1034f16d4"
SRC_URI = "git://git.kernel.org/pub/scm/linux/kernel/git/kdave/btrfs-progs.git \
           file://0001-Makefile-build-mktables-using-native-gcc.patch \
           file://0001-Fix-build-with-musl-missing-header-include-for-dev_t.patch \
           "

RECIPE_NO_UPDATE_REASON = "Waiting for resolution of https://github.com/kdave/btrfs-progs/issues/109"
inherit autotools-brokensep pkgconfig manpages

CLEANBROKEN = "1"

PACKAGECONFIG[manpages] = "--enable-documentation, --disable-documentation, asciidoc-native xmlto-native"
EXTRA_OECONF_append_libc-musl = " --disable-backtrace "

do_configure_prepend() {
	# Upstream doesn't ship this and autoreconf won't install it as automake isn't used.
	mkdir -p ${S}/config
	cp -f $(automake --print-libdir)/install-sh ${S}/config/
}

S = "${WORKDIR}/git"

BBCLASSEXTEND = "native"
