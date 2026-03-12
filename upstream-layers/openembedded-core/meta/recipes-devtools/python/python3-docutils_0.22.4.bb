SUMMARY = "Docutils is a modular system for processing documentation into useful formats"
HOMEPAGE = "http://docutils.sourceforge.net"
SECTION = "devel/python"
LICENSE = "CC0-1.0 & BSD-2-Clause & GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING.rst;md5=ce467b04b35c7ac3429b6908fc8b318e"

SRC_URI[sha256sum] = "4db53b1fde9abecbb74d91230d32ab626d94f6badfc575d6db9194a49df29968"

inherit pypi python_flit_core

RDEPENDS:${PN} += "python3-pprint"

# We don't install the emacs lisp, which is the only piece of GPLv3
LICENSE:${PN} = "CC0-1.0 & BSD-2-Clause"

BBCLASSEXTEND = "native nativesdk"
