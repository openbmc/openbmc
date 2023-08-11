SUMMARY = "Screen-scraping library"
HOMEPAGE = " https://www.crummy.com/software/BeautifulSoup/bs4"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=96e0034f7c9443910c486773aa1ed9ac"

SRC_URI[sha256sum] = "492bbc69dca35d12daac71c4db1bfff0c876c00ef4a2ffacce226d4638eb72da"

inherit pypi python_hatchling

RDEPENDS:${PN} = "\
    ${PYTHON_PN}-html5lib \
    ${PYTHON_PN}-lxml \
    ${PYTHON_PN}-soupsieve \
    ${PYTHON_PN}-html \
    ${PYTHON_PN}-logging \
"

BBCLASSEXTEND = "native nativesdk"
