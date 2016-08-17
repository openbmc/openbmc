SUMMARY = "World timezone definitions, modern and historical"
HOMEPAGE = " http://pythonhosted.org/pytz"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=22b38951eb857cf285a4560a914b7cd6"

inherit pypi setuptools

RDEPENDS_${PN} = "\
    python-core \
    python-datetime \
"

SRC_URI[md5sum] = "abae92c3301b27bd8a9f56b14f52cb29"
SRC_URI[sha256sum] = "3449da19051655d4c0bb5c37191331748bcad15804d81676a88451ef299370a8"
