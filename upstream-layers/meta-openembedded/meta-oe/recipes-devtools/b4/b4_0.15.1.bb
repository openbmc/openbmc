SUMMARY = "Utility to work with patches made available via a public-inbox archive like lore.kernel.org."

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit pypi python_pep517 python_setuptools_build_meta

SRC_URI[sha256sum] = "e48c44bb579fadabb1fc3f15bf1874afd721bc1a63fba10c96f568bc1f47ccb3"

RDEPENDS:${PN} += " \
    python3-mailbox \
    python3-requests \
"
