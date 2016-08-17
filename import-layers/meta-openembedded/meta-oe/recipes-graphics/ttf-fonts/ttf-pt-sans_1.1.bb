SUMMARY = "PT Sans Fonts"
DESCRIPTION = "The PT Sans TTF font set"
HOMEPAGE = "http://www.paratype.com/public/"

SECTION = "x11/fonts"

LICENSE = "ParaTypeFFL-1.3"
LIC_FILES_CHKSUM = "file://../PT%20Free%20Font%20License_eng.txt;md5=d720f3a281ed81c3f4cfc465e11b1d0d"

inherit allarch fontcache

# Downloading from fedora because upstream doesn't version its zip file
# and causes hash build failures
SRC_URI = "http://pkgs.fedoraproject.org/repo/pkgs/paratype-pt-sans-fonts/PTSans.zip/c3f5a0e20a75cf628387510a720924a7/PTSans.zip"

SRC_URI[md5sum] = "c3f5a0e20a75cf628387510a720924a7"
SRC_URI[sha256sum] = "0164f824e03c32c99e8a225853ec168893a04a09ade132e93a674e85ae033b2e"

do_install () {
    install -d ${D}${datadir}/fonts/X11/TTF/
    cd ..
    for i in *.ttf; do
        install -m 0644 $i ${D}${prefix}/share/fonts/X11/TTF/${i}
    done
}

FILES_${PN} += "${datadir}"

pkg_postinst_${PN} () {
    set -x
    for fontdir in `find $D/usr/lib/X11/fonts -type d`; do
        mkfontdir $fontdir
        mkfontscale $fontdir
    done
    for fontdir in `find $D/usr/share/fonts/X11 -type d`; do
        mkfontdir $fontdir
        mkfontscale $fontdir
    done
}
