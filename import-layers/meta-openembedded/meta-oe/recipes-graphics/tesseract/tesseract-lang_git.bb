SUMMARY = "tesseract-ocr language files"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=9648bd7af63bd3cc4f5ac046d12c49e4"

PV = "3.04.00+git${SRCPV}"
SRCREV = "3cf1e2df1fe1d1da29295c9ef0983796c7958b7d"
SRC_URI = "git://github.com/tesseract-ocr/tessdata.git"
S = "${WORKDIR}/git"

inherit allarch

do_install() {
    install -d ${D}${datadir}/tessdata
    cp -R --no-dereference --preserve=mode,links -v ${S}/*.traineddata ${S}/*.cube.* ${S}/*.tesseract_cube.* ${D}${datadir}/tessdata
}

python populate_packages_prepend () {
    tessdata_dir= d.expand('${datadir}/tessdata')
    pkgs = do_split_packages(d, tessdata_dir, '^([a-z_]*)\.*', '${BPN}-%s', 'tesseract-ocr language files for %s', extra_depends='')
    pn = d.getVar('PN')
    d.appendVar('RDEPENDS_' + pn, ' '+' '.join(pkgs))
}

PACKAGES_DYNAMIC += "^${BPN}-.*"
ALLOW_EMPTY_${PN} = "1"
