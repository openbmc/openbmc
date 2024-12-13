SUMMARY = "Common GNOME build files for C++ bindings"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"


inherit gnomebase

# All the recipe does is stage python and shell script, some autotools files; nothing is compiled.
inherit allarch

SRC_URI[archive.sha256sum] = "b55c46037dbcdabc5cee3b389ea11cc3910adb68ebe883e9477847aa660862e7"

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
