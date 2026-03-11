SUMMARY = "Universal Office Converter - Office document conversion"
HOMEPAGE = "https://github.com/unoconv/unoconv"
LICENSE = "GPL-2.0-only"
SECTION = "devel/python"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

SRC_URI[sha256sum] = "308ebfd98e67d898834876348b27caf41470cd853fbe2681cc7dacd8fd5e6031"

inherit pypi setuptools3

PYPI_PACKAGE = "unoconv"

RDEPENDS:${PN} += "\
    python3-setuptools \
    python3-core \
    python3-shell \
"

do_install:append() {
  sed -i -e 's:^#!/usr/bin/env python$:#!/usr/bin/env python3:' ${D}/usr/bin/unoconv
}

BBCLASSEXTEND = "native nativesdk"
