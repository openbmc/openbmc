SUMMARY = "Screen-scraping library"
HOMEPAGE = " https://www.crummy.com/software/BeautifulSoup/bs4"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=96e0034f7c9443910c486773aa1ed9ac"

SRC_URI[sha256sum] = "6292b1c5186d356bba669ef9f7f051757099565ad9ada5dd630bd9de5fa7fb86"

inherit pypi python_hatchling

RDEPENDS:${PN} = "\
    python3-html5lib \
    python3-lxml \
    python3-html \
    python3-logging \
"
RDEPENDS:${PN}:append:class-target = " \
    python3-soupsieve \
"

BBCLASSEXTEND = "native nativesdk"
