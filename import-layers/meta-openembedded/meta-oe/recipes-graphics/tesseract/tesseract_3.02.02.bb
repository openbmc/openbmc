DESCRIPTION = "A commercial quality OCR engine "

DEPENDS = "leptonica"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=c0c94b3c86ad0c386bb6dec70ca8a36a"

SRC_URI = "https://tesseract-ocr.googlecode.com/files/tesseract-ocr-${PV}.tar.gz"
SRC_URI[md5sum] = "26adc8154f0e815053816825dde246e6"
SRC_URI[sha256sum] = "26cd39cb3f2a6f6f1bf4050d1cc0aae35edee49eb49a92df3cb7f9487caa013d"

EXTRA_OECONF += "LIBLEPT_HEADERSDIR=${STAGING_INCDIR}/leptonica"

S = "${WORKDIR}/tesseract-ocr"

inherit autotools pkgconfig

FILES_${PN} += "${datadir}/tessdata"

RRECOMMENDS_${PN} += "tesseract-lang-eng"

# http://errors.yoctoproject.org/Errors/Details/35134/
PNBLACKLIST[tesseract] ?= "BROKEN: QA Issue: tesseract.pc failed sanity test (tmpdir)"
