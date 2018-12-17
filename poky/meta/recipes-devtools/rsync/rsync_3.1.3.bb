SUMMARY = "File synchronization tool"
HOMEPAGE = "http://rsync.samba.org/"
BUGTRACKER = "http://rsync.samba.org/bugzilla.html"
SECTION = "console/network"
# GPLv2+ (<< 3.0.0), GPLv3+ (>= 3.0.0)
LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

DEPENDS = "popt"

SRC_URI = "https://download.samba.org/pub/${BPN}/src/${BP}.tar.gz \
           file://rsyncd.conf \
           file://makefile-no-rebuild.patch \
"

SRC_URI[md5sum] = "1581a588fde9d89f6bc6201e8129afaf"
SRC_URI[sha256sum] = "55cc554efec5fdaad70de921cd5a5eeb6c29a95524c715f3bbf849235b0800c0"

inherit autotools

PACKAGECONFIG ??= "acl attr \
    ${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)} \
"

PACKAGECONFIG[acl] = "--enable-acl-support,--disable-acl-support,acl,"
PACKAGECONFIG[attr] = "--enable-xattr-support,--disable-xattr-support,attr,"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"

# By default, if crosscompiling, rsync disables a number of
# capabilities, hardlinking symlinks and special files (i.e. devices)
CACHED_CONFIGUREVARS += "rsync_cv_can_hardlink_special=yes rsync_cv_can_hardlink_symlink=yes"

EXTRA_OEMAKE = 'STRIP=""'

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

BBCLASSEXTEND = "native"
