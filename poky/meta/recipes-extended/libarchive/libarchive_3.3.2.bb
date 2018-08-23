SUMMARY = "Support for reading various archive formats"
DESCRIPTION = "C library and command-line tools for reading and writing tar, cpio, zip, ISO, and other archive formats"
HOMEPAGE = "http://www.libarchive.org/"
SECTION = "devel"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=ed99aca006bc346974bb745a35336425"

DEPENDS = "e2fsprogs-native"

PACKAGECONFIG ?= "zlib bz2 xz lzo"

PACKAGECONFIG_append_class-target = "\
	libxml2 \
	${@bb.utils.filter('DISTRO_FEATURES', 'acl xattr', d)} \
"

DEPENDS_BZIP2 = "bzip2-replacement-native"
DEPENDS_BZIP2_class-target = "bzip2"

PACKAGECONFIG[acl] = "--enable-acl,--disable-acl,acl,"
PACKAGECONFIG[xattr] = "--enable-xattr,--disable-xattr,attr,"
PACKAGECONFIG[zlib] = "--with-zlib,--without-zlib,zlib,"
PACKAGECONFIG[bz2] = "--with-bz2lib,--without-bz2lib,${DEPENDS_BZIP2},"
PACKAGECONFIG[xz] = "--with-lzma,--without-lzma,xz,"
PACKAGECONFIG[openssl] = "--with-openssl,--without-openssl,openssl,"
PACKAGECONFIG[libxml2] = "--with-xml2,--without-xml2,libxml2,"
PACKAGECONFIG[expat] = "--with-expat,--without-expat,expat,"
PACKAGECONFIG[lzo] = "--with-lzo2,--without-lzo2,lzo,"
PACKAGECONFIG[nettle] = "--with-nettle,--without-nettle,nettle,"
PACKAGECONFIG[lz4] = "--with-lz4,--without-lz4,lz4,"

EXTRA_OECONF += "--enable-largefile"

SRC_URI = "http://libarchive.org/downloads/libarchive-${PV}.tar.gz \
           file://bug929.patch \
           file://CVE-2017-14166.patch \
           file://CVE-2017-14502.patch \
           file://non-recursive-extract-and-list.patch \
           file://CVE-2017-14503.patch \
           file://CVE-2017-14501.patch \
          "

SRC_URI[md5sum] = "4583bd6b2ebf7e0e8963d90879eb1b27"
SRC_URI[sha256sum] = "ed2dbd6954792b2c054ccf8ec4b330a54b85904a80cef477a1c74643ddafa0ce"

inherit autotools update-alternatives pkgconfig

CPPFLAGS += "-I${WORKDIR}/extra-includes"

do_configure[cleandirs] += "${WORKDIR}/extra-includes"
do_configure_prepend() {
	# We just need the headers for some type constants, so no need to
	# build all of e2fsprogs for the target
	cp -R ${STAGING_INCDIR_NATIVE}/ext2fs ${WORKDIR}/extra-includes/
}

ALTERNATIVE_PRIORITY = "80"

PACKAGES =+ "bsdtar"
FILES_bsdtar = "${bindir}/bsdtar"

ALTERNATIVE_bsdtar = "tar"
ALTERNATIVE_LINK_NAME[tar] = "${base_bindir}/tar"
ALTERNATIVE_TARGET[tar] = "${bindir}/bsdtar"

PACKAGES =+ "bsdcpio"
FILES_bsdcpio = "${bindir}/bsdcpio"

ALTERNATIVE_bsdcpio = "cpio"
ALTERNATIVE_LINK_NAME[cpio] = "${base_bindir}/cpio"
ALTERNATIVE_TARGET[cpio] = "${bindir}/bsdcpio"

BBCLASSEXTEND = "native nativesdk"
