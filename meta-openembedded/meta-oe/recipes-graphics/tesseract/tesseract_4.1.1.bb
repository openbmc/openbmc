SUMMARY = "A commercial quality OCR engine "
HOMEPAGE = "https://github.com/tesseract-ocr/tesseract"
BUGTRACKER = "https://github.com/tesseract-ocr/tesseract/issues"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

BRANCH = "4.1"
SRCREV = "f4ef2f2050f4c25b28bdbf0063b7d2eb30f41cf7"
SRC_URI = "git://github.com/${BPN}-ocr/${BPN}.git;branch=${BRANCH} \
           file://0001-include-sys-time.h.patch \
          "

S = "${WORKDIR}/git"

DEPENDS = "leptonica"

EXTRA_OECONF += "LIBLEPT_HEADERSDIR=${STAGING_INCDIR}/leptonica"

inherit autotools pkgconfig

FILES_${PN} += "${datadir}/tessdata"

RRECOMMENDS_${PN} += "tesseract-lang-eng"
