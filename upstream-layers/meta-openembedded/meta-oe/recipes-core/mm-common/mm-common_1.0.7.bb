SUMMARY = "Common GNOME build files for C++ bindings"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=570a9b3749dd0463a1778803b12a6dce"


inherit gnomebase

# All the recipe does is stage python and shell script, some autotools files; nothing is compiled.
inherit allarch

SRC_URI[archive.sha256sum] = "494abfce781418259b1e9d8888c73af4de4b6f3be36cc75d9baa8baa0f2a7a39"

BBCLASSEXTEND = "native nativesdk"

# These files aren't very usefull on target image, package them all in nnPN-dev
FILES:${PN} = ""

FILES:${PN}-dev += " \
    ${datadir}/${BPN}/build \
    ${datadir}/${BPN}/doctags \
    ${datadir}/${BPN}/doctool \
    ${bindir}/mm-common-get \
    ${bindir}/mm-common-prepare \
"
# ${PN} package is empty, remove the default dependency on it
RDEPENDS:${PN}-dev = ""
