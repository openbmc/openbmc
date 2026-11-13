SUMMARY = "A commercial quality OCR engine "
HOMEPAGE = "https://github.com/tesseract-ocr/tesseract"
BUGTRACKER = "https://github.com/tesseract-ocr/tesseract/issues"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRCREV = "7e548e6eff172f38f97664afe7f7ce40db36c8d6"
SRC_URI = "git://github.com/${BPN}-ocr/${BPN}.git;branch=main;protocol=https;tag=${PV} \
"

DEPENDS = "leptonica"

EXTRA_OECONF += "LIBLEPT_HEADERSDIR=${STAGING_INCDIR}/leptonica"

inherit autotools pkgconfig

FILES:${PN} += "${datadir}/tessdata"

RRECOMMENDS:${PN} += "tesseract-lang-eng"

BBCLASSEXTEND = "native"
