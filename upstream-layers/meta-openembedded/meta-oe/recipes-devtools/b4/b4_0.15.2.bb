SUMMARY = "Utility to work with patches made available via a public-inbox archive like lore.kernel.org."

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit pypi python_pep517 python_setuptools_build_meta

SRC_URI[sha256sum] = "b815f2aed2288718cfe2a14c76421a00bc4f0918ea32b45dd1645c999fdda69d"

RDEPENDS:${PN} += " \
    python3-mailbox \
    python3-requests \
"
