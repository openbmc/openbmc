SUMMARY = "Utility to work with patches made available via a public-inbox archive like lore.kernel.org."

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit pypi python_pep517 python_setuptools_build_meta

SRC_URI[sha256sum] = "4f835b6e5ae30eff6004bb25c15fd8f4f6ecd1105596e86db1871fef7d18113d"

RDEPENDS:${PN} += " \
    python3-mailbox \
    python3-requests \
"
