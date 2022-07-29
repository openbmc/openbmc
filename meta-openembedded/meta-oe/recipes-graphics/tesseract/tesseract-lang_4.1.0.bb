SUMMARY = "tesseract-ocr language files"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRCREV = "4767ea922bcc460e70b87b1d303ebdfed0897da8"
SRC_URI = "git://github.com/tesseract-ocr/tessdata.git;branch=main;protocol=https"

S = "${WORKDIR}/git"

inherit allarch

do_install() {
    install -d ${D}${datadir}/tessdata
    cp -R --no-dereference --preserve=mode,links -v ${S}/*.traineddata ${D}${datadir}/tessdata
}

python populate_packages:prepend () {
    tessdata_dir= d.expand('${datadir}/tessdata')
    pkgs = do_split_packages(d, tessdata_dir, r'^([a-z_]*)\.*', '${BPN}-%s', 'tesseract-ocr language files for %s', extra_depends='')
    pn = d.getVar('PN')
    d.appendVar('RDEPENDS:' + pn, ' '+' '.join(pkgs))
}

PACKAGES_DYNAMIC += "^${BPN}-.*"
ALLOW_EMPTY:${PN} = "1"
