SUMMARY = "Screen-scraping library"
HOMEPAGE = " https://www.crummy.com/software/BeautifulSoup/bs4"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING.txt;md5=f2d38d8a40bf73fd4b3d16ca2e5882d1"

SRC_URI[md5sum] = "c71f53fcb2580c376ab7b010a9178983"
SRC_URI[sha256sum] = "945065979fb8529dd2f37dbb58f00b661bdbcbebf954f93b32fdf5263ef35348"

inherit pypi

RDEPENDS_${PN}_class-target = "\
    ${PYTHON_PN}-core \
    ${PYTHON_PN}-html5lib \
    ${PYTHON_PN}-lxml \
"

BBCLASSEXTEND = "native nativesdk"
