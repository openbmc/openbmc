SUMMARY = "An extended commonmark compliant parser, with bridges to docutils/sphinx"
HOMEPAGE = "https://github.com/executablebooks/MyST-Parser"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a38a1697260a7ad7bf29f44b362db1fc"

SRC_URI[sha256sum] = "ab69322dc6719dcc7f296479dbb70181b66df6ed315064f92dbc85c0e1bf2f02"

inherit pypi python_flit_core

PYPI_PACKAGE = "myst_parser"

RDEPENDS:${PN} = "\
    python3-docutils \
    python3-jinja2 \
    python3-markdown-it-py \
    python3-mdit-py-plugins \
    python3-pyyaml \
    python3-sphinx \
    "

BBCLASSEXTEND = "native nativesdk"
