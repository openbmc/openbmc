SUMMARY = "Screen-scraping library"
HOMEPAGE = " https://www.crummy.com/software/BeautifulSoup/bs4"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING.txt;md5=f2d38d8a40bf73fd4b3d16ca2e5882d1"

SRC_URI[md5sum] = "57502b5b34ccfd97b180260071f5799a"
SRC_URI[sha256sum] = "73cc4d115b96f79c7d77c1c7f7a0a8d4c57860d1041df407dd1aae7f07a77fd7"

inherit pypi setuptools3

RDEPENDS_${PN} = "\
    ${PYTHON_PN}-html5lib \
    ${PYTHON_PN}-lxml \
    ${PYTHON_PN}-soupsieve \
"

BBCLASSEXTEND = "native nativesdk"
