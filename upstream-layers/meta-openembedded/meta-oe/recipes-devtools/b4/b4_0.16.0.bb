SUMMARY = "Utility to work with patches made available via a public-inbox archive like lore.kernel.org."

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit pypi python_pep517 python_setuptools_build_meta

DEPENDS += "python3-wheel-native"

SRC_URI[sha256sum] = "071823a1e904508a6fd9aaf8cc2f9a92697e1dfa270000b4d1130015b56f4137"

RDEPENDS:${PN} += " \
    python3-mailbox \
    python3-requests \
"
