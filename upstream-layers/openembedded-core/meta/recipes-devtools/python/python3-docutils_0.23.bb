SUMMARY = "Docutils is a modular system for processing documentation into useful formats"
HOMEPAGE = "http://docutils.sourceforge.net"
SECTION = "devel/python"
LICENSE = "BSD-2-Clause AND CC0-1.0 AND GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING.rst;md5=ce467b04b35c7ac3429b6908fc8b318e"

SRC_URI[sha256sum] = "746f5060322511280a1e50eb76846ed6bf2342984b2ac04dc42caa1a8d78799e"

inherit pypi python_flit_core

RDEPENDS:${PN} += "python3-pprint"

# We don't install the emacs lisp, which is the only piece of GPLv3
LICENSE:${PN} = "BSD-2-Clause AND CC0-1.0"

BBCLASSEXTEND = "native nativesdk"
