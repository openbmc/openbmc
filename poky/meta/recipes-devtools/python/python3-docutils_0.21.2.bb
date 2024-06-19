SUMMARY = "Docutils is a modular system for processing documentation into useful formats"
HOMEPAGE = "http://docutils.sourceforge.net"
SECTION = "devel/python"
LICENSE = "CC0-1.0 & ZPL-2.1 & BSD-2-Clause & GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING.txt;md5=382430a09a4453818aa6618f2090491b"

SRC_URI[sha256sum] = "3a6b18732edf182daa3cd12775bbb338cf5691468f91eeeb109deff6ebfa986f"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += " \
                   python3-pprint \
"
BBCLASSEXTEND = "native nativesdk"
