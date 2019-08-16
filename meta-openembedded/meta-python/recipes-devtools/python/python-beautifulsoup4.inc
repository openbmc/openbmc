SUMMARY = "Screen-scraping library"
HOMEPAGE = " https://www.crummy.com/software/BeautifulSoup/bs4"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING.txt;md5=f2d38d8a40bf73fd4b3d16ca2e5882d1"

SRC_URI[md5sum] = "2b34208b26b5c49d4c84a4b9cf78656a"
SRC_URI[sha256sum] = "25288c9e176f354bf277c0a10aa96c782a6a18a17122dba2e8cec4a97e03343b"

inherit pypi

RDEPENDS_${PN}_class-target = "\
    ${PYTHON_PN}-core \
    ${PYTHON_PN}-html5lib \
    ${PYTHON_PN}-lxml \
"

BBCLASSEXTEND = "native nativesdk"
