SUMMARY = "A tiny Sphinx extension that implements blog-style tags for documentation"
HOMEPAGE = "https://sphinx-tags.readthedocs.io/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=05c148fce9144d129e885beada2a6c94"

SRC_URI[sha256sum] = "3065219bacf47567c706f2237d5665b22f3a5167367b4c4568bcda4371a5359d"

inherit pypi python_flit_core

RDEPENDS:${PN} += "python3-sphinx-design"

BBCLASSEXTEND = "native nativesdk"
