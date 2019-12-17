SUMMARY = "Network monitoring tools"
DESCRIPTION = "Utilities for the IP protocol, including traceroute6, \
tracepath, tracepath6, ping, ping6 and arping."
HOMEPAGE = "https://github.com/iputils/iputils"
SECTION = "console/network"

LICENSE = "BSD & GPLv2+"

LIC_FILES_CHKSUM = "file://LICENSE;md5=55aa8c9fcad0691cef0ecd420361e390"

DEPENDS = "gnutls"

SRC_URI = "git://github.com/iputils/iputils"
SRCREV = "13e00847176aa23683d68fce1d17ffb523510946"

S = "${WORKDIR}/git"

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>s\d+)"

# Fixed in 2000-10-10, but the versioning of iputils
# breaks the version order.
CVE_CHECK_WHITELIST += "CVE-2000-1213 CVE-2000-1214"

PACKAGECONFIG ??= "libcap libgcrypt rarpd traceroute6"
PACKAGECONFIG[libcap] = "-DUSE_CAP=true, -DUSE_CAP=false, libcap"
PACKAGECONFIG[libgcrypt] = "-DUSE_CRYPTO=gcrypt, -DUSE_CRYPTO=none, libgcrypt"
PACKAGECONFIG[libidn] = "-DUSE_IDN=true, -DUSE_IDN=false, libidn2"
PACKAGECONFIG[gettext] = "-DUSE_GETTEXT=true, -DUSE_GETTEXT=false, gettext"
PACKAGECONFIG[rarpd] = "-DBUILD_RARPD=true,-DBUILD_RARPD=false,"
PACKAGECONFIG[traceroute6] = "-DBUILD_TRACEROUTE6=true,-DBUILD_TRACEROUTE6=false,"
PACKAGECONFIG[docs] = "-DBUILD_HTML_MANS=true -DBUILD_MANS=true,-DBUILD_HTML_MANS=false -DBUILD_MANS=false, libxslt"

inherit meson update-alternatives

EXTRA_OEMESON += "--prefix=${root_prefix}/"

ALTERNATIVE_PRIORITY = "100"

ALTERNATIVE_${PN}-ping = "ping"
ALTERNATIVE_LINK_NAME[ping] = "${base_bindir}/ping"

SPLITPKGS = "${PN}-ping ${PN}-arping ${PN}-tracepath ${PN}-traceroute6 ${PN}-clockdiff ${PN}-tftpd ${PN}-rarpd ${PN}-rdisc ${PN}-ninfod"
PACKAGES += "${SPLITPKGS}"

ALLOW_EMPTY_${PN} = "1"
ALLOW_EMPTY_${PN}-rarpd = "1"
ALLOW_EMPTY_${PN}-traceroute6 = "1"
RDEPENDS_${PN} += "${SPLITPKGS}"

FILES_${PN} = ""
FILES_${PN}-ping = "${base_bindir}/ping.${BPN}"
FILES_${PN}-arping = "${base_bindir}/arping"
FILES_${PN}-tracepath = "${base_bindir}/tracepath"
FILES_${PN}-traceroute6	= "${base_bindir}/traceroute6"
FILES_${PN}-clockdiff = "${base_bindir}/clockdiff"
FILES_${PN}-tftpd = "${base_bindir}/tftpd"
FILES_${PN}-rarpd = "${base_sbindir}/rarpd"
FILES_${PN}-rdisc = "${base_sbindir}/rdisc"
FILES_${PN}-ninfod = "${base_sbindir}/ninfod ${sysconfdir}/init.d/ninfod.sh"
