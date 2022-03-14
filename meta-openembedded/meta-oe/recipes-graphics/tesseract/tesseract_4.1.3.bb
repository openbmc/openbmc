SUMMARY = "A commercial quality OCR engine "
HOMEPAGE = "https://github.com/tesseract-ocr/tesseract"
BUGTRACKER = "https://github.com/tesseract-ocr/tesseract/issues"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

BRANCH = "4.1"
SRCREV = "f38e7a7ba850b668d4505dd4c712238d7ec63ca8"
SRC_URI = "git://github.com/${BPN}-ocr/${BPN}.git;branch=${BRANCH};protocol=https \
           file://0001-include-sys-time.h.patch \
          "

S = "${WORKDIR}/git"

DEPENDS = "leptonica"

EXTRA_OECONF += "LIBLEPT_HEADERSDIR=${STAGING_INCDIR}/leptonica"

inherit autotools pkgconfig

FILES:${PN} += "${datadir}/tessdata"

RRECOMMENDS:${PN} += "tesseract-lang-eng"
