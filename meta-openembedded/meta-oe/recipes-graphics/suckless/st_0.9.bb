SECTION = "shadow"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=703e9835709f45ee7b81082277f1daec"
SRC_URI = "http://dl.suckless.org/${BPN}/${BP}.tar.gz"

inherit pkgconfig features_check

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[sha256sum] = "f36359799734eae785becb374063f0be833cf22f88b4f169cd251b99324e08e7"

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
