SUMMARY = "Support for reading various archive formats"
DESCRIPTION = "C library and command-line tools for reading and writing tar, cpio, zip, ISO, and other archive formats"
HOMEPAGE = "http://www.libarchive.org/"
SECTION = "devel"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=b4e3ffd607d6686c6cb2f63394370841"

DEPENDS = "e2fsprogs-native"

PACKAGECONFIG ?= "libxml2 zlib bz2"

PACKAGECONFIG_append_class-target = "\
	${@bb.utils.contains('DISTRO_FEATURES', 'acl', 'acl', '', d)} \
	${@bb.utils.contains('DISTRO_FEATURES', 'xattr', 'xattr', '', d)} \
	${@bb.utils.contains('DISTRO_FEATURES', 'largefile', 'largefile', '', d)} \
"

PACKAGECONFIG_append_class-nativesdk = " largefile"

PACKAGECONFIG[acl] = "--enable-acl,--disable-acl,acl,"
PACKAGECONFIG[xattr] = "--enable-xattr,--disable-xattr,attr,"
PACKAGECONFIG[largefile] = "--enable-largefile,--disable-largefile,,"
PACKAGECONFIG[zlib] = "--with-zlib,--without-zlib,zlib,"
PACKAGECONFIG[bz2] = "--with-bz2lib,--without-bz2lib,bzip2,"
PACKAGECONFIG[xz] = "--with-lzmadec --with-lzma,--without-lzmadec --without-lzma,xz,"
PACKAGECONFIG[openssl] = "--with-openssl,--without-openssl,openssl,"
PACKAGECONFIG[libxml2] = "--with-xml2,--without-xml2,libxml2,"
PACKAGECONFIG[expat] = "--with-expat,--without-expat,expat,"
PACKAGECONFIG[lzo] = "--with-lzo2,--without-lzo2,lzo,"
PACKAGECONFIG[nettle] = "--with-nettle,--without-nettle,nettle,"

SRC_URI = "http://libarchive.org/downloads/libarchive-${PV}.tar.gz \
           file://libarchive-CVE-2013-0211.patch \
           file://pkgconfig.patch \
           file://libarchive-CVE-2015-2304.patch \
           file://mkdir.patch \
           "

SRC_URI[md5sum] = "efad5a503f66329bb9d2f4308b5de98a"
SRC_URI[sha256sum] = "eb87eacd8fe49e8d90c8fdc189813023ccc319c5e752b01fb6ad0cc7b2c53d5e"

inherit autotools lib_package pkgconfig

CPPFLAGS += "-I${WORKDIR}/extra-includes"

do_configure[cleandirs] += "${WORKDIR}/extra-includes"
do_configure_prepend() {
	# We just need the headers for some type constants, so no need to
	# build all of e2fsprogs for the target
	cp -R ${STAGING_INCDIR_NATIVE}/ext2fs ${WORKDIR}/extra-includes/
}

BBCLASSEXTEND = "native nativesdk"
