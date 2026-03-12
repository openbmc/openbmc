require ttf.inc

SUMMARY = "MPlus font - TTF Edition"
HOMEPAGE = "https://mplusfonts.github.io/"
LICENSE = "OFL-1.1"
LIC_FILES_CHKSUM = "file://OFL.txt;md5=ee870a4a7cee012360178b2f8bccb725"

SRC_URI = "git://github.com/coz-m/MPLUS_FONTS.git;protocol=https;branch=master"
SRCREV = "80ac404d0c80442781b6f4c6119a8c9e71770806"

PACKAGESPLITFUNCS:prepend = "split_ttf_mplus_packages "

python split_ttf_mplus_packages() {
    plugindir = d.expand('${datadir}/fonts/ttf-mplus/')
    packages = do_split_packages(d, plugindir, r'^(.*)\.ttf$', 'ttf-%s', 'TTF Font %s')
    d.setVar('FONT_PACKAGES', ' '.join(packages))
}

do_install() {
    install -d ${D}${datadir}/fonts/ttf-mplus
    cd fonts/ttf
    for f in *.ttf; do
        install -m 0644 $f ${D}${datadir}/fonts/ttf-mplus/"$(echo "$f" | tr '[]' '_')"
    done
}
