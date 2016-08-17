SUMMARY = "Internationalised Domain Names in Applications"
HOMEPAGE = "https://github.com/kjd/idna"
LICENSE = "BSD-3-Clause & Python-2.0 & Unicode"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=134bdad79491c37bdae32811572b4bc6"

DEPENDS += "python-pip"

SRC_URI[md5sum] = "f6473caa9c5e0cc1ad3fd5d04c3c114b"
SRC_URI[sha256sum] = "ed36f281aebf3cd0797f163bb165d84c31507cedd15928b095b1675e2d04c676"

inherit pypi setuptools
