require ttf.inc

SUMMARY = "MPlus font - TTF Edition"
HOMEPAGE = "http://dejavu.sourceforge.net/wiki/"
LICENSE = "${BPN}"
LIC_FILES_CHKSUM = "file://LICENSE_E;md5=ac161e96eda00db9a3aec7870b5d9658 \
                    file://LICENSE_J;md5=a120ca8d7c8e4a475d5277c9aeb95221 \
"
SRC_URI = "http://downloads.sourceforge.jp/mplus-fonts/6650/mplus-TESTFLIGHT-${PV}.tar.gz"
S = "${WORKDIR}/mplus-TESTFLIGHT-${PV}"

PACKAGESPLITFUNCS:prepend = "split_ttf_mplus_packages "

python split_ttf_mplus_packages() {
    plugindir = d.expand('${datadir}/fonts/ttf-mplus/')
    packages = do_split_packages(d, plugindir, r'^(.*)\.ttf$', 'ttf-%s', 'TTF Font %s')
    d.setVar('FONT_PACKAGES', ' '.join(packages))
}

do_install() {
    install -d ${D}${datadir}/fonts/ttf-mplus
    install -m 0644 *.ttf ${D}${datadir}/fonts/ttf-mplus/
}

SRC_URI[md5sum] = "d1400184b51b3871e8d2fca6c50e18ae"
SRC_URI[sha256sum] = "a20b9b9b03c2a6fb1e2137d29e8a6ce06406ba1e008906ea3c474dc048dc06a6"
