SUMMARY = "Checksumming Copy on Write Filesystem utilities"
DESCRIPTION = "Btrfs is a new copy on write filesystem for Linux aimed at \
implementing advanced features while focusing on fault tolerance, repair and \
easy administration. \
This package contains utilities (mkfs, fsck, btrfsctl) used to work with \
btrfs and an utility (btrfs-convert) to make a btrfs filesystem from an ext3."

HOMEPAGE = "https://btrfs.wiki.kernel.org"

LICENSE = "GPL-2.0-only & LGPL-2.1-or-later"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=fcb02dc552a041dee27e4b85c7396067 \
    file://libbtrfsutil/COPYING;md5=4fbd65380cdd255951079008b364516c \
"
SECTION = "base"
DEPENDS = "util-linux zlib"

SRC_URI = "git://github.com/kdave/btrfs-progs.git;branch=master;protocol=https \
           file://0001-Add-a-possibility-to-specify-where-python-modules-ar.patch \
           "
SRCREV = "85ca0a6d60c14eefda509970a26616ff16115612"
S = "${WORKDIR}/git"

PACKAGECONFIG ??= " \
    programs \
    convert \
    python \
    crypto-builtin \
"
PACKAGECONFIG[manpages] = "--enable-documentation, --disable-documentation, python3-sphinx-native python3-sphinx-rtd-theme-native"
PACKAGECONFIG[programs] = "--enable-programs,--disable-programs"
PACKAGECONFIG[convert] = "--enable-convert --with-convert=ext2,--disable-convert --without-convert,e2fsprogs"
PACKAGECONFIG[zoned] = "--enable-zoned,--disable-zoned"
PACKAGECONFIG[python] = "--enable-python,--disable-python,python3-setuptools-native"
PACKAGECONFIG[lzo] = "--enable-lzo,--disable-lzo,lzo"
PACKAGECONFIG[zstd] = "--enable-zstd,--disable-zstd,zstd"
PACKAGECONFIG[udev] = "--enable-libudev,--disable-libudev,udev"

# Pick only one crypto provider
PACKAGECONFIG[crypto-builtin] = "--with-crypto=builtin"
PACKAGECONFIG[crypto-libgcrypt] = "--with-crypto=libgcrypt,,libgcrypt"
PACKAGECONFIG[crypto-libsodium] = "--with-crypto=libsodium,,libsodium"
PACKAGECONFIG[crypto-libkcapi] = "--with-crypto=libkcapi,,libkcapi"

inherit autotools-brokensep pkgconfig manpages
inherit_defer ${@bb.utils.contains('PACKAGECONFIG', 'python', 'setuptools3-base', '', d)}

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
