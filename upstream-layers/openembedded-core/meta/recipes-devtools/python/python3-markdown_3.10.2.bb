SUMMARY = "A Python implementation of John Gruber's Markdown."
HOMEPAGE = "https://python-markdown.github.io/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=ec58cdf7cfed06a21f7a9362627a5480"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "994d51325d25ad8aa7ce4ebaec003febcce822c3f8c911e3b17c52f7f589f950"

BBCLASSEXTEND = "native nativesdk"

RDEPENDS:${PN} += "python3-logging python3-setuptools"
