SUMMARY = "A commercial quality OCR engine "
HOMEPAGE = "https://github.com/tesseract-ocr/tesseract"
BUGTRACKER = "https://github.com/tesseract-ocr/tesseract/issues"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRCREV = "e082522c248d3121e466959a8ba4fd4f7ad1a525"
SRC_URI = "git://github.com/${BPN}-ocr/${BPN}.git;branch=main;protocol=https"

S = "${WORKDIR}/git"

DEPENDS = "leptonica"

EXTRA_OECONF += "LIBLEPT_HEADERSDIR=${STAGING_INCDIR}/leptonica"

inherit autotools pkgconfig

FILES:${PN} += "${datadir}/tessdata"

RRECOMMENDS:${PN} += "tesseract-lang-eng"
