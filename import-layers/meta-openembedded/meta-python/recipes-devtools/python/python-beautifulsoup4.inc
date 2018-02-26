SUMMARY = "Screen-scraping library"
HOMEPAGE = " https://www.crummy.com/software/BeautifulSoup/bs4"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING.txt;md5=f2d38d8a40bf73fd4b3d16ca2e5882d1"

SRC_URI[md5sum] = "c17714d0f91a23b708a592cb3c697728"
SRC_URI[sha256sum] = "808b6ac932dccb0a4126558f7dfdcf41710dd44a4ef497a0bb59a77f9f078e89"

inherit pypi

RDEPENDS_${PN}_class-target = "\
    ${PYTHON_PN}-core \
    ${PYTHON_PN}-html5lib \
    ${PYTHON_PN}-lxml \
"

BBCLASSEXTEND = "native nativesdk"
