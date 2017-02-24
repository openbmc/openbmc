SUMMARY = "A commercial quality OCR engine "

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=c0c94b3c86ad0c386bb6dec70ca8a36a"

PV = "3.04.00+git${SRCPV}"
SRCREV = "228317caa1ced217e6d264aafc901f361ecd7e90"
SRC_URI = "git://github.com/${BPN}-ocr/${BPN}.git"
S = "${WORKDIR}/git"

DEPENDS = "leptonica"

EXTRA_OECONF += "LIBLEPT_HEADERSDIR=${STAGING_INCDIR}/leptonica"


inherit autotools pkgconfig

FILES_${PN} += "${datadir}/tessdata"

RRECOMMENDS_${PN} += "tesseract-lang-eng"
