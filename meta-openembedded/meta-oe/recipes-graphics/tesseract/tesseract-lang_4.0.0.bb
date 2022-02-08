SUMMARY = "tesseract-ocr language files"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=9648bd7af63bd3cc4f5ac046d12c49e4"

SRCREV = "590567f20dc044f6948a8e2c61afc714c360ad0e"
SRC_URI = "git://github.com/tesseract-ocr/tessdata.git;branch=master;protocol=https"

S = "${WORKDIR}/git"

inherit allarch

do_install() {
    install -d ${D}${datadir}/tessdata
    cp -R --no-dereference --preserve=mode,links -v ${S}/*.traineddata ${D}${datadir}/tessdata
}

python populate_packages_prepend () {
    tessdata_dir= d.expand('${datadir}/tessdata')
    pkgs = do_split_packages(d, tessdata_dir, '^([a-z_]*)\.*', '${BPN}-%s', 'tesseract-ocr language files for %s', extra_depends='')
    pn = d.getVar('PN')
    d.appendVar('RDEPENDS_' + pn, ' '+' '.join(pkgs))
}

PACKAGES_DYNAMIC += "^${BPN}-.*"
ALLOW_EMPTY_${PN} = "1"
