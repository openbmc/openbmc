SUMMARY = "Network monitoring tools"
DESCRIPTION = "Utilities for the IP protocol, including traceroute6, \
tracepath, tracepath6, ping, ping6 and arping."
HOMEPAGE = "http://www.skbuff.net/iputils"
SECTION = "console/network"

LICENSE = "BSD & GPLv2+"

LIC_FILES_CHKSUM = "file://ping.c;beginline=1;endline=35;md5=f9ceb201733e9a6cf8f00766dd278d82 \
                    file://tracepath.c;beginline=1;endline=10;md5=0ecea2bf60bff2f3d840096d87647f3d \
                    file://arping.c;beginline=1;endline=11;md5=fe84301b5c2655c950f8b92a057fafa6 \
                    file://tftpd.c;beginline=1;endline=32;md5=28834bf8a91a5b8a92755dbee709ef96 "

DEPENDS = "gnutls docbook-utils-native sgmlspl-native libcap libgcrypt"


SRC_URI = "http://www.skbuff.net/iputils/${BPN}-${PV}.tar.bz2 \
           file://debian/use_gethostbyname2.diff \
           file://debian/targets.diff \
           file://nsgmls-path-fix.patch \
           file://0001-Fix-header-inclusion-for-musl.patch \
           file://0001-Intialize-struct-elements-by-name.patch \
          "

SRC_URI[md5sum] = "8aaa7395f27dff9f57ae016d4bc753ce"
SRC_URI[sha256sum] = "549f58d71951e52b46595829134d4e330642f522f50026917fadc349a54825a1"

UPSTREAM_CHECK_REGEX = "iputils-(?P<pver>s\d+).tar"

EXTRA_OEMAKE = "-e MAKEFLAGS="

do_compile () {
	oe_runmake 'CC=${CC} -D_GNU_SOURCE' VPATH="${STAGING_LIBDIR}:${STAGING_DIR_HOST}/${base_libdir}" all man
}

do_install () {
	install -m 0755 -d ${D}${base_bindir} ${D}${mandir}/man8
	# SUID root programs
	install -m 4555 ping ${D}${base_bindir}/ping
	install -m 4555 ping6 ${D}${base_bindir}/ping6
	install -m 4555 traceroute6 ${D}${base_bindir}/
	install -m 4555 clockdiff ${D}${base_bindir}/
	# Other programgs
	for i in arping tracepath tracepath6; do
	  install -m 0755 $i ${D}${base_bindir}/
	done
	# Manual pages for things we build packages for
	for i in tracepath.8 traceroute6.8 ping.8 arping.8; do
	  install -m 0644 doc/$i ${D}${mandir}/man8/ || true
	done
}

inherit update-alternatives

ALTERNATIVE_PRIORITY = "100"

ALTERNATIVE_${PN}-ping = "ping"
ALTERNATIVE_LINK_NAME[ping] = "${base_bindir}/ping"

ALTERNATIVE_${PN}-ping6 = "ping6"
ALTERNATIVE_LINK_NAME[ping6] = "${base_bindir}/ping6"

PACKAGES += "${PN}-ping ${PN}-ping6 ${PN}-arping ${PN}-tracepath ${PN}-tracepath6 ${PN}-traceroute6 ${PN}-clockdiff"

ALLOW_EMPTY_${PN} = "1"
RDEPENDS_${PN} += "${PN}-ping ${PN}-ping6 ${PN}-arping ${PN}-tracepath ${PN}-tracepath6 ${PN}-traceroute6 ${PN}-clockdiff"

FILES_${PN}	= ""
FILES_${PN}-ping = "${base_bindir}/ping.${BPN}"
FILES_${PN}-ping6 = "${base_bindir}/ping6.${BPN}"
FILES_${PN}-arping = "${base_bindir}/arping"
FILES_${PN}-tracepath = "${base_bindir}/tracepath"
FILES_${PN}-tracepath6 = "${base_bindir}/tracepath6"
FILES_${PN}-traceroute6	= "${base_bindir}/traceroute6"
FILES_${PN}-clockdiff = "${base_bindir}/clockdiff"
FILES_${PN}-doc	= "${mandir}/man8"
