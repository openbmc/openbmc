SECTION = "shadow"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=703e9835709f45ee7b81082277f1daec"
SRC_URI = "http://dl.suckless.org/${BPN}/${BP}.tar.gz"

inherit pkgconfig features_check

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[sha256sum] = "6b215d4f472b21d6232f30f221117a777e24bcfee68955ddefb7426467f9494b"

DEPENDS += "libx11 libxft fontconfig ncurses-native"

RDEPENDS:${PN} += "libx11-locale"

do_compile() {
    make INCS='-I. `pkg-config --cflags x11 fontconfig xft`' LIBS='-lm -lutil `pkg-config --libs x11 fontconfig xft`'
}
do_install() {
    make install DESTDIR=${D} PREFIX=/usr TERMINFO=${D}${datadir}/terminfo
}

FILES:${PN} += " \
    ${datadir}/terminfo \
"

inherit update-alternatives

ALTERNATIVE_PRIORITY = "50"

ALTERNATIVE:${PN} = "st st-256color"

ALTERNATIVE_LINK_NAME[st] = "${datadir}/terminfo/s/st"

ALTERNATIVE_LINK_NAME[st-256color] = "${datadir}/terminfo/s/st-256color"

CVE_STATUS[CVE-2017-16224] = "cpe-incorrect: The recipe used in the meta-openembedded is a different st package compared to the one which has the CVE issue."
