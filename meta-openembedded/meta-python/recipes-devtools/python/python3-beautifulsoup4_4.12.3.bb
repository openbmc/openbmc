SUMMARY = "Screen-scraping library"
HOMEPAGE = " https://www.crummy.com/software/BeautifulSoup/bs4"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=96e0034f7c9443910c486773aa1ed9ac"

SRC_URI[sha256sum] = "74e3d1928edc070d21748185c46e3fb33490f22f52a3addee9aee0f4f7781051"

inherit pypi python_hatchling

RDEPENDS:${PN} = "\
    python3-html5lib \
    python3-lxml \
    python3-soupsieve \
    python3-html \
    python3-logging \
"

BBCLASSEXTEND = "native nativesdk"
