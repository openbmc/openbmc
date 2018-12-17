SUMMARY = "Network monitoring tools"
DESCRIPTION = "Utilities for the IP protocol, including traceroute6, \
tracepath, tracepath6, ping, ping6 and arping."
HOMEPAGE = "https://github.com/iputils/iputils"
SECTION = "console/network"

LICENSE = "BSD & GPLv2+"

LIC_FILES_CHKSUM = "file://LICENSE;md5=b792e38abdc59f766a3153908f23e766 \
                    file://LICENSE.BSD3;md5=0f00d99239d922ffd13cabef83b33444 \
                    file://LICENSE.GPL2;md5=b234ee4d69f5fce4486a80fdaf4a4263"

DEPENDS = "gnutls libcap libgcrypt"

SRC_URI = "git://github.com/iputils/iputils \
           file://ai_canonidn.patch \
           file://install.patch"
SRCREV = "f6aac8dbe3f8c45c53424854a3312bdd8cdd58d3"

S = "${WORKDIR}/git"

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>s\d+)"

EXTRA_OEMAKE = "-e MAKEFLAGS="

PACKAGECONFIG ??= ""
PACKAGECONFIG[libidn] = "USE_IDN=yes,USE_IDN=no,libidn2"

do_compile () {
	oe_runmake 'CC=${CC} -D_GNU_SOURCE' VPATH="${STAGING_LIBDIR}:${STAGING_DIR_HOST}/${base_libdir}" ${PACKAGECONFIG_CONFARGS} all
}

do_install() {
	oe_runmake DESTDIR=${D} bindir=${base_bindir} install
	for b in ping traceroute6 clockdiff; do
		chmod u+s ${D}${base_bindir}/$b
	done
}

inherit update-alternatives

ALTERNATIVE_PRIORITY = "100"

ALTERNATIVE_${PN}-ping = "ping"
ALTERNATIVE_LINK_NAME[ping] = "${base_bindir}/ping"

SPLITPKGS = "${PN}-ping ${PN}-arping ${PN}-tracepath ${PN}-traceroute6 ${PN}-clockdiff ${PN}-tftpd ${PN}-rarpd ${PN}-rdisc"
PACKAGES += "${SPLITPKGS}"

ALLOW_EMPTY_${PN} = "1"
RDEPENDS_${PN} += "${SPLITPKGS}"

FILES_${PN}	= ""
FILES_${PN}-ping = "${base_bindir}/ping.${BPN}"
FILES_${PN}-arping = "${base_bindir}/arping"
FILES_${PN}-tracepath = "${base_bindir}/tracepath"
FILES_${PN}-traceroute6	= "${base_bindir}/traceroute6"
FILES_${PN}-clockdiff = "${base_bindir}/clockdiff"
FILES_${PN}-tftpd = "${base_bindir}/tftpd"
FILES_${PN}-rarpd = "${base_bindir}/rarpd"
FILES_${PN}-rdisc = "${base_bindir}/rdisc"
