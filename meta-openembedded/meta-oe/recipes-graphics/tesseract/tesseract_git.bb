SUMMARY = "A commercial quality OCR engine "

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=7ea4f9a43aba9d3c849fe5c203a0ed40"

BRANCH = "3.05"
PV = "${BRANCH}.01+git${SRCPV}"
SRCREV = "215866151e774972c9502282111b998d7a053562"
SRC_URI = "git://github.com/${BPN}-ocr/${BPN}.git;branch=${BRANCH};protocol=https"
S = "${WORKDIR}/git"

DEPENDS = "leptonica"

EXTRA_OECONF += "LIBLEPT_HEADERSDIR=${STAGING_INCDIR}/leptonica"


inherit autotools pkgconfig

FILES_${PN} += "${datadir}/tessdata"

RRECOMMENDS_${PN} += "tesseract-lang-eng"
