SUMMARY = "Network monitoring tools"
DESCRIPTION = "Utilities for the IP protocol, including traceroute6, \
tracepath, tracepath6, ping, ping6 and arping."
HOMEPAGE = "https://github.com/iputils/iputils"
SECTION = "console/network"

LICENSE = "BSD & GPLv2+"

LIC_FILES_CHKSUM = "file://LICENSE;md5=55aa8c9fcad0691cef0ecd420361e390"

DEPENDS = "gnutls"

SRC_URI = "git://github.com/iputils/iputils \
           file://0001-rarpd-rdisc-Drop-PrivateUsers.patch \
           file://0001-meson-Make-tests-optional.patch \
           "
SRCREV = "71bb2a6c72e9f658e90ac618c73d873a76bbaa81"

S = "${WORKDIR}/git"

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>20\d+)"

# Fixed in 2000-10-10, but the versioning of iputils
# breaks the version order.
CVE_CHECK_WHITELIST += "CVE-2000-1213 CVE-2000-1214"

PACKAGECONFIG ??= "libcap rarpd \
                   ${@bb.utils.contains('DISTRO_FEATURES', 'ipv6', 'ninfod traceroute6', '', d)} \
                   ${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"
PACKAGECONFIG[libcap] = "-DUSE_CAP=true, -DUSE_CAP=false -DNO_SETCAP_OR_SUID=true, libcap libcap-native"
PACKAGECONFIG[libidn] = "-DUSE_IDN=true, -DUSE_IDN=false, libidn2"
PACKAGECONFIG[gettext] = "-DUSE_GETTEXT=true, -DUSE_GETTEXT=false, gettext"
PACKAGECONFIG[ninfod] = "-DBUILD_NINFOD=true,-DBUILD_NINFOD=false,"
PACKAGECONFIG[rarpd] = "-DBUILD_RARPD=true,-DBUILD_RARPD=false,"
PACKAGECONFIG[systemd] = "-Dsystemdunitdir=${systemd_unitdir}/system,,systemd"
PACKAGECONFIG[tftpd] = "-DBUILD_TFTPD=true, -DBUILD_TFTPD=false,"
PACKAGECONFIG[traceroute6] = "-DBUILD_TRACEROUTE6=true,-DBUILD_TRACEROUTE6=false,"
PACKAGECONFIG[docs] = "-DBUILD_HTML_MANS=true -DBUILD_MANS=true,-DBUILD_HTML_MANS=false -DBUILD_MANS=false, libxslt"

inherit meson systemd update-alternatives

EXTRA_OEMESON += "--prefix=${root_prefix}/ -DSKIP_TESTS=true"

ALTERNATIVE_PRIORITY = "100"

ALTERNATIVE:${PN}-ping = "ping"
ALTERNATIVE_LINK_NAME[ping] = "${base_bindir}/ping"

SPLITPKGS = "${PN}-ping ${PN}-arping ${PN}-tracepath ${PN}-clockdiff ${PN}-rdisc \
             ${@bb.utils.contains('PACKAGECONFIG', 'rarpd', '${PN}-rarpd', '', d)} \
             ${@bb.utils.contains('PACKAGECONFIG', 'tftpd', '${PN}-tftpd', '', d)} \
             ${@bb.utils.contains('DISTRO_FEATURES', 'ipv6', '${PN}-traceroute6 ${PN}-ninfod', '', d)}"
PACKAGES += "${SPLITPKGS}"

ALLOW_EMPTY:${PN} = "1"
RDEPENDS:${PN} += "${SPLITPKGS}"

FILES:${PN} = ""
FILES:${PN}-ping = "${base_bindir}/ping.${BPN}"
FILES:${PN}-arping = "${base_bindir}/arping"
FILES:${PN}-tracepath = "${base_bindir}/tracepath"
FILES:${PN}-traceroute6	= "${base_bindir}/traceroute6"
FILES:${PN}-clockdiff = "${base_bindir}/clockdiff"
FILES:${PN}-tftpd = "${base_bindir}/tftpd ${sysconfdir}/xinetd.d/tftp"
FILES:${PN}-rarpd = "${base_sbindir}/rarpd  ${systemd_unitdir}/system/rarpd@.service"
FILES:${PN}-rdisc = "${base_sbindir}/rdisc"
FILES:${PN}-ninfod = "${base_sbindir}/ninfod ${sysconfdir}/init.d/ninfod.sh"

SYSTEMD_PACKAGES = "${@bb.utils.contains('DISTRO_FEATURES', 'ipv6', '${PN}-ninfod', '', d)} \
                    ${PN}-rdisc"
SYSTEMD_SERVICE:${PN}-ninfod = "ninfod.service"
SYSTEMD_SERVICE:${PN}-rdisc = "rdisc.service"
