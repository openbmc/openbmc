SUMMARY = "fwknop - Single Packet Authorization"
HOMEPAGE = "http://www.cipherdyne.org/fwknop/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552 \
		    "
inherit autotools-brokensep

SRC_URI = "http://www.cipherdyne.org/${BPN}/download/${BPN}-${PV}.tar.bz2 \
          "
SRC_URI[md5sum] = "47a9c7c214c40dceb5dc2aa8832e4f32"
SRC_URI[sha256sum] = "f6c09bec97ed8e474a98ae14f9f53e1bcdda33393f20667b6af3fb6bb894ca77"

DEPENDS = "libpcap gpgme"

EXTRA_OECONF = " --with-iptables=/usr/sbin/iptables \
               "

do_configure () {
	install -m 0755 ${STAGING_DATADIR_NATIVE}/gnu-config/config.guess ${S}/config
	install -m 0755 ${STAGING_DATADIR_NATIVE}/gnu-config/config.sub ${S}/config

	aclocal
	libtoolize --automake --copy --force
	autoconf
	autoheader
	automake -a
	oe_runconf
}

PACKAGES =+ "${PN}-client ${PN}-daemon"

FILES_${PN}-client = "${bindir}/fwknop"
FILES_${PN}-daemon = "${sbindir}/fwknopd \
                      ${sysconfdir}/fwknop/access.conf \
                      ${sysconfdir}/fwknop/fwknopd.conf"

