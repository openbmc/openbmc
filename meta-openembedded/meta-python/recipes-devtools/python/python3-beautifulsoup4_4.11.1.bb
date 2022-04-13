SUMMARY = "Screen-scraping library"
HOMEPAGE = " https://www.crummy.com/software/BeautifulSoup/bs4"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING.txt;md5=15a76c7c51ecfc5c094d04f3ccd41a09"

SRC_URI[sha256sum] = "ad9aa55b65ef2808eb405f46cf74df7fcb7044d5cbc26487f96eb2ef2e436693"

inherit pypi setuptools3

RDEPENDS:${PN} = "\
    ${PYTHON_PN}-html5lib \
    ${PYTHON_PN}-lxml \
    ${PYTHON_PN}-soupsieve \
    ${PYTHON_PN}-html \
    ${PYTHON_PN}-logging \
"

BBCLASSEXTEND = "native nativesdk"
