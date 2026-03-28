SUMMARY = "Utility to work with patches made available via a public-inbox archive like lore.kernel.org."

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit pypi python_pep517 python_setuptools_build_meta

SRC_URI[sha256sum] = "4cfd99a27af9cabe319fe21ba20af695f68c65904c63731c4fc5a30ea38da7c8"

RDEPENDS:${PN} += " \
    python3-mailbox \
    python3-requests \
"
