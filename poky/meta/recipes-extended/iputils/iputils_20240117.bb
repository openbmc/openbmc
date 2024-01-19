SUMMARY = "Network monitoring tools"
DESCRIPTION = "Utilities for the IP protocol, including \
tracepath, tracepath6, ping, ping6 and arping."
HOMEPAGE = "https://github.com/iputils/iputils"
SECTION = "console/network"

LICENSE = "BSD-3-Clause & GPL-2.0-or-later"

LIC_FILES_CHKSUM = "file://LICENSE;md5=627cc07ec86a45951d43e30658bbd819"

DEPENDS = "gnutls"

SRC_URI = "git://github.com/iputils/iputils;branch=master;protocol=https"
SRCREV = "8372f355bdf7a9b0c79338dd8ef8464c00a5c4e2"

S = "${WORKDIR}/git"

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>20\d+)"

CVE_STATUS[CVE-2000-1213] = "fixed-version: Fixed in 2000-10-10, but the versioning of iputils breaks the version order."
CVE_STATUS[CVE-2000-1214] = "fixed-version: Fixed in 2000-10-10, but the versioning of iputils breaks the version order."

PACKAGECONFIG ??= "libcap"
PACKAGECONFIG[libcap] = "-DUSE_CAP=true, -DUSE_CAP=false -DNO_SETCAP_OR_SUID=true, libcap libcap-native"
PACKAGECONFIG[libidn] = "-DUSE_IDN=true, -DUSE_IDN=false, libidn2"
PACKAGECONFIG[gettext] = "-DUSE_GETTEXT=true, -DUSE_GETTEXT=false, gettext"
PACKAGECONFIG[docs] = "-DBUILD_HTML_MANS=true -DBUILD_MANS=true,-DBUILD_HTML_MANS=false -DBUILD_MANS=false, libxslt"

inherit meson update-alternatives pkgconfig

EXTRA_OEMESON += "--prefix=${root_prefix}/ -DSKIP_TESTS=true"

ALTERNATIVE_PRIORITY = "100"

ALTERNATIVE:${PN}-ping = "ping"
ALTERNATIVE_LINK_NAME[ping] = "${base_bindir}/ping"

SPLITPKGS = "${PN}-ping ${PN}-arping ${PN}-tracepath ${PN}-clockdiff"
PACKAGES += "${SPLITPKGS}"

ALLOW_EMPTY:${PN} = "1"
RDEPENDS:${PN} += "${SPLITPKGS}"

FILES:${PN} = ""
FILES:${PN}-ping = "${base_bindir}/ping.${BPN}"
FILES:${PN}-arping = "${base_bindir}/arping"
FILES:${PN}-tracepath = "${base_bindir}/tracepath"
FILES:${PN}-clockdiff = "${base_bindir}/clockdiff"
