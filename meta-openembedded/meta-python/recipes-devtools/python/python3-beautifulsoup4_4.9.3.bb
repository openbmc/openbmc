SUMMARY = "Screen-scraping library"
HOMEPAGE = " https://www.crummy.com/software/BeautifulSoup/bs4"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING.txt;md5=f2d38d8a40bf73fd4b3d16ca2e5882d1"

SRC_URI[md5sum] = "57fd468ae3eb055f6871106e8f7813e2"
SRC_URI[sha256sum] = "84729e322ad1d5b4d25f805bfa05b902dd96450f43842c4e99067d5e1369eb25"

inherit pypi setuptools3

RDEPENDS_${PN} = "\
    ${PYTHON_PN}-html5lib \
    ${PYTHON_PN}-lxml \
    ${PYTHON_PN}-soupsieve \
"

BBCLASSEXTEND = "native nativesdk"
