SUMMARY = "Checksumming Copy on Write Filesystem utilities"
DESCRIPTION = "Btrfs is a new copy on write filesystem for Linux aimed at \
implementing advanced features while focusing on fault tolerance, repair and \
easy administration. \
This package contains utilities (mkfs, fsck, btrfsctl) used to work with \
btrfs and an utility (btrfs-convert) to make a btrfs filesystem from an ext3."

HOMEPAGE = "https://btrfs.wiki.kernel.org"

LICENSE = "GPLv2 & LGPLv2.1+"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=fcb02dc552a041dee27e4b85c7396067 \
    file://libbtrfsutil/COPYING;md5=4fbd65380cdd255951079008b364516c \
"
SECTION = "base"
DEPENDS = "lzo util-linux zlib"

SRC_URI = "git://git.kernel.org/pub/scm/linux/kernel/git/kdave/btrfs-progs.git;branch=master \
           file://0001-Add-a-possibility-to-specify-where-python-modules-ar.patch \
           file://0001-btrfs-progs-kerncompat-add-local-definition-for-alig.patch \
           "
SRCREV = "8ad326b2f28c044cb6ed9016d7c3285e23b673c8"
S = "${WORKDIR}/git"

PACKAGECONFIG ??= " \
    programs \
    convert \
    python \
    crypto-builtin \
"
PACKAGECONFIG[manpages] = "--enable-documentation, --disable-documentation, asciidoc-native xmlto-native"
PACKAGECONFIG[programs] = "--enable-programs,--disable-programs"
PACKAGECONFIG[convert] = "--enable-convert --with-convert=ext2,--disable-convert --without-convert,e2fsprogs"
PACKAGECONFIG[zoned] = "--enable-zoned,--disable-zoned"
PACKAGECONFIG[python] = "--enable-python,--disable-python,python3-setuptools-native"
PACKAGECONFIG[zstd] = "--enable-zstd,--disable-zstd,zstd"
PACKAGECONFIG[udev] = "--enable-libudev,--disable-libudev,udev"

# Pick only one crypto provider
PACKAGECONFIG[crypto-builtin] = "--with-crypto=builtin"
PACKAGECONFIG[crypto-libgcrypt] = "--with-crypto=libgcrypt,,libgcrypt"
PACKAGECONFIG[crypto-libsodium] = "--with-crypto=libsodium,,libsodium"
PACKAGECONFIG[crypto-libkcapi] = "--with-crypto=libkcapi,,libkcapi"

inherit autotools-brokensep pkgconfig manpages
inherit ${@bb.utils.contains('PACKAGECONFIG', 'python', 'setuptools3-base', '', d)}

CLEANBROKEN = "1"

EXTRA_OECONF = "--enable-largefile"
EXTRA_OECONF:append:libc-musl = " --disable-backtrace "
EXTRA_PYTHON_CFLAGS = "${DEBUG_PREFIX_MAP}"
EXTRA_PYTHON_CFLAGS:class-native = ""
EXTRA_PYTHON_LDFLAGS = "${LDFLAGS}"
EXTRA_OEMAKE = "V=1 'EXTRA_PYTHON_CFLAGS=${EXTRA_PYTHON_CFLAGS}' 'EXTRA_PYTHON_LDFLAGS=${EXTRA_PYTHON_LDFLAGS}'"

do_configure:prepend() {
	# Upstream doesn't ship this and autoreconf won't install it as automake isn't used.
	mkdir -p ${S}/config
	cp -f $(automake --print-libdir)/install-sh ${S}/config/
}


do_install:append() {
    if [ "${@bb.utils.filter('PACKAGECONFIG', 'python', d)}" ]; then
        oe_runmake 'DESTDIR=${D}' 'PYTHON_SITEPACKAGES_DIR=${PYTHON_SITEPACKAGES_DIR}' install_python
    fi
}

RDEPENDS:${PN} = "libgcc"

BBCLASSEXTEND = "native nativesdk"
