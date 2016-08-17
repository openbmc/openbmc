LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

SRC_URI = "https://tesseract-ocr.googlecode.com/files/tesseract-ocr-${PV}.${TESSERACT_LANG}.tar.gz"

S = "${WORKDIR}/tesseract-ocr"

inherit allarch

do_install() {
    install -d ${D}${datadir}/tessdata
    cp ${S}/tessdata/${TESSERACT_LANG}.traineddata ${D}${datadir}/tessdata
}

FILES_${PN} += "${datadir}/tessdata"
