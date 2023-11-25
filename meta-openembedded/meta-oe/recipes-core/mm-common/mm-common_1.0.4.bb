SUMMARY = "Common GNOME build files for C++ bindings"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"


inherit gnomebase

# All the recipe does is stage python and shell script, some autotools files; nothing is compiled.
inherit allarch

SRC_URI[archive.sha256sum] = "e954c09b4309a7ef93e13b69260acdc5738c907477eb381b78bb1e414ee6dbd8"
SRC_URI += "file://0001-meson.build-do-not-ask-for-python-installation-versi.patch"

BBCLASSEXTEND = "native"

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
