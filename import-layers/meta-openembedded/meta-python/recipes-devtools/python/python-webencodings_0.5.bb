SUMMARY = "Character encoding aliases for legacy web content"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://PKG-INFO;startline=8;endline=9;md5=2fc6c99a19e6dbde9f7a2239143c9d13"

SRC_URI[md5sum] = "878714d45241f7970dffd8991d61fff9"
SRC_URI[sha256sum] = "a5c55ee93b24e740fe951c37b5c228dccc1f171450e188555a775261cce1b904"

inherit pypi setuptools

RDEPENDS_${PN} += "${PYTHON_PN}-codecs"
