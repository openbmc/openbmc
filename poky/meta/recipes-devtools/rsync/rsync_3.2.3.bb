SUMMARY = "File synchronization tool"
HOMEPAGE = "http://rsync.samba.org/"
DESCRIPTION = "rsync is an open source utility that provides fast incremental file transfer."
BUGTRACKER = "http://rsync.samba.org/bugzilla.html"
SECTION = "console/network"
# GPLv2+ (<< 3.0.0), GPLv3+ (>= 3.0.0)
# Includes opennsh and xxhash dynamic link exception
LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=9e5a4f9b3a253d51520617aa54f8eb26"

DEPENDS = "popt"

SRC_URI = "https://download.samba.org/pub/${BPN}/src/${BP}.tar.gz \
           file://rsyncd.conf \
           file://makefile-no-rebuild.patch \
           file://determism.patch \
           "

SRC_URI[sha256sum] = "becc3c504ceea499f4167a260040ccf4d9f2ef9499ad5683c179a697146ce50e"

# -16548 required for v3.1.3pre1. Already in v3.1.3.
CVE_CHECK_WHITELIST += " CVE-2017-16548 "

inherit autotools-brokensep

PACKAGECONFIG ??= "acl attr \
    ${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)} \
"

PACKAGECONFIG[acl] = "--enable-acl-support,--disable-acl-support,acl,"
PACKAGECONFIG[attr] = "--enable-xattr-support,--disable-xattr-support,attr,"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"
PACKAGECONFIG[lz4] = "--enable-lz4,--disable-lz4,lz4"
PACKAGECONFIG[openssl] = "--enable-openssl,--disable-openssl,openssl"
PACKAGECONFIG[xxhash] = "--enable-xxhash,--disable-xxhash,xxhash"
PACKAGECONFIG[zstd] = "--enable-zstd,--disable-zstd,zstd"

# By default, if crosscompiling, rsync disables a number of
# capabilities, hardlinking symlinks and special files (i.e. devices)
CACHED_CONFIGUREVARS += "rsync_cv_can_hardlink_special=yes rsync_cv_can_hardlink_symlink=yes"

EXTRA_OEMAKE = 'STRIP=""'
EXTRA_OECONF = "--disable-simd --disable-md2man --disable-asm --with-nobody-group=nogroup"

# rsync 3.0 uses configure.sh instead of configure, and
# makefile checks the existence of configure.sh
do_configure_prepend () {
	rm -f ${S}/configure ${S}/configure.sh
}

do_configure_append () {
	cp -f ${S}/configure ${S}/configure.sh
}

do_install_append() {
	install -d ${D}${sysconfdir}
	install -m 0644 ${WORKDIR}/rsyncd.conf ${D}${sysconfdir}
}

BBCLASSEXTEND = "native nativesdk"
