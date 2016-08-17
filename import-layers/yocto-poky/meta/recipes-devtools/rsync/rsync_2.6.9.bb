SUMMARY = "File synchronization tool"
HOMEPAGE = "http://rsync.samba.org/"
BUGTRACKER = "http://rsync.samba.org/bugzilla.html"
SECTION = "console/network"

# needs to add acl and attr
DEPENDS = "popt"

SRC_URI = "http://rsync.samba.org/ftp/rsync/src/rsync-${PV}.tar.gz \
           file://rsync-2.6.9-fname-obo.patch \
           file://rsyncd.conf"

SRC_URI[md5sum] = "996d8d8831dbca17910094e56dcb5942"
SRC_URI[sha256sum] = "ca437301becd890e73300bc69a39189ff1564baa761948ff149b3dd7bde633f9"

inherit autotools

do_install_append() {
	install -d ${D}${sysconfdir}
	install -m 0644 ${WORKDIR}/rsyncd.conf ${D}${sysconfdir}
}

EXTRA_OEMAKE='STRIP=""'

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=6d5a9d4c4d3af25cd68fd83e8a8cb09c"

PR = "r4"
