SUMMARY = "File synchronization tool"
HOMEPAGE = "http://rsync.samba.org/"
BUGTRACKER = "http://rsync.samba.org/bugzilla.html"
SECTION = "console/network"

# GPLv2+ (<< 3.0.0), GPLv3+ (>= 3.0.0)
LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

# needs to add acl and attr
DEPENDS = "popt"

SRC_URI = "http://rsync.samba.org/ftp/rsync/src/rsync-${PV}.tar.gz \
           file://rsyncd.conf"

inherit autotools

do_install_append() {
	install -d ${D}${sysconfdir}
	install -m 0644 ${WORKDIR}/rsyncd.conf ${D}${sysconfdir}
}

EXTRA_OEMAKE='STRIP=""'
