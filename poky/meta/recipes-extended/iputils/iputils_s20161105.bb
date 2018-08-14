SUMMARY = "Network monitoring tools"
DESCRIPTION = "Utilities for the IP protocol, including traceroute6, \
tracepath, tracepath6, ping, ping6 and arping."
HOMEPAGE = "https://github.com/iputils/iputils"
SECTION = "console/network"

LICENSE = "BSD & GPLv2+"

LIC_FILES_CHKSUM = "file://ping.c;beginline=1;endline=35;md5=f9ceb201733e9a6cf8f00766dd278d82 \
                    file://tracepath.c;beginline=1;endline=10;md5=0ecea2bf60bff2f3d840096d87647f3d \
                    file://arping.c;beginline=1;endline=11;md5=fe84301b5c2655c950f8b92a057fafa6 \
                    file://tftpd.c;beginline=1;endline=32;md5=28834bf8a91a5b8a92755dbee709ef96 "

DEPENDS = "gnutls libcap libgcrypt"

SRC_URI = "git://github.com/iputils/iputils \
           file://0001-Fix-build-on-MUSL.patch \
           "
S = "${WORKDIR}/git"
SRCREV = "bffc0e957b98d626ab4cea218c89251201425442"

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>s\d+)"

EXTRA_OEMAKE = "-e MAKEFLAGS="

PACKAGECONFIG ?= ""
PACKAGECONFIG[libidn] = "USE_IDN=yes,USE_IDN=no,libidn"

do_compile () {
	oe_runmake 'CC=${CC} -D_GNU_SOURCE' VPATH="${STAGING_LIBDIR}:${STAGING_DIR_HOST}/${base_libdir}" ${PACKAGECONFIG_CONFARGS} all
}

do_install () {
	install -m 0755 -d ${D}${base_bindir}
	# SUID root programs
	install -m 4555 ping ${D}${base_bindir}/ping
	install -m 4555 traceroute6 ${D}${base_bindir}/
	install -m 4555 clockdiff ${D}${base_bindir}/
	# Other programgs
	for i in arping tracepath; do
	  install -m 0755 $i ${D}${base_bindir}/
	done
}

inherit update-alternatives

ALTERNATIVE_PRIORITY = "100"

ALTERNATIVE_${PN}-ping = "ping"
ALTERNATIVE_LINK_NAME[ping] = "${base_bindir}/ping"

PACKAGES += "${PN}-ping ${PN}-arping ${PN}-tracepath ${PN}-traceroute6 ${PN}-clockdiff"

ALLOW_EMPTY_${PN} = "1"
RDEPENDS_${PN} += "${PN}-ping ${PN}-arping ${PN}-tracepath ${PN}-traceroute6 ${PN}-clockdiff"

FILES_${PN}	= ""
FILES_${PN}-ping = "${base_bindir}/ping.${BPN}"
FILES_${PN}-arping = "${base_bindir}/arping"
FILES_${PN}-tracepath = "${base_bindir}/tracepath"
FILES_${PN}-traceroute6	= "${base_bindir}/traceroute6"
FILES_${PN}-clockdiff = "${base_bindir}/clockdiff"
