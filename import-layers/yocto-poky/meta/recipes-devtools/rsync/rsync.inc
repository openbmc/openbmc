SUMMARY = "File synchronization tool"
HOMEPAGE = "http://rsync.samba.org/"
BUGTRACKER = "http://rsync.samba.org/bugzilla.html"
SECTION = "console/network"

DEPENDS = "popt"

SRC_URI = "https://download.samba.org/pub/${BPN}/src/${BP}.tar.gz \
           file://rsyncd.conf"

inherit autotools

do_install_append() {
	install -d ${D}${sysconfdir}
	install -m 0644 ${WORKDIR}/rsyncd.conf ${D}${sysconfdir}
}

EXTRA_OEMAKE='STRIP=""'

PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"
