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
DEPENDS = "util-linux attr e2fsprogs lzo acl python3-setuptools-native"
DEPENDS_append_class-target = " udev"
RDEPENDS_${PN} = "libgcc"

SRCREV = "781e36a784faa58a4f0515eef124af860d59e2c0"
SRC_URI = "git://git.kernel.org/pub/scm/linux/kernel/git/kdave/btrfs-progs.git \
           file://0001-Add-a-possibility-to-specify-where-python-modules-ar.patch \
           "

inherit autotools-brokensep pkgconfig manpages distutils3-base

CLEANBROKEN = "1"

PACKAGECONFIG[manpages] = "--enable-documentation, --disable-documentation, asciidoc-native xmlto-native"
EXTRA_OECONF = " --disable-zstd"
EXTRA_OECONF_append_libc-musl = " --disable-backtrace "
EXTRA_PYTHON_CFLAGS = "${DEBUG_PREFIX_MAP}"
EXTRA_PYTHON_CFLAGS_class-native = ""
EXTRA_PYTHON_LDFLAGS = "${LDFLAGS}"
EXTRA_OEMAKE = "V=1 'EXTRA_PYTHON_CFLAGS=${EXTRA_PYTHON_CFLAGS}' 'EXTRA_PYTHON_LDFLAGS=${EXTRA_PYTHON_LDFLAGS}'"

do_configure_prepend() {
	# Upstream doesn't ship this and autoreconf won't install it as automake isn't used.
	mkdir -p ${S}/config
	cp -f $(automake --print-libdir)/install-sh ${S}/config/
}

S = "${WORKDIR}/git"

do_install_append() {
    oe_runmake 'DESTDIR=${D}' 'PYTHON_SITEPACKAGES_DIR=${PYTHON_SITEPACKAGES_DIR}' install_python
}

BBCLASSEXTEND = "native"
