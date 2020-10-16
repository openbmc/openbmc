SECTION = "shadow"
LICENSE = "MIT-X"
LIC_FILES_CHKSUM = "file://LICENSE;md5=04c3ca13a702147c62db90f556c5b3ca"
SRC_URI = "http://dl.suckless.org/${BPN}/${BP}.tar.gz"

inherit pkgconfig features_check

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[sha256sum] = "d42d3ceceb4d6a65e32e90a5336e3d446db612c3fbd9ebc1780bc6c9a03346a6"

DEPENDS += "libx11 libxft fontconfig ncurses-native"

RDEPENDS_${PN} += "libx11-locale"

do_compile() {
    make INCS='-I. `pkg-config --cflags x11 fontconfig xft`' LIBS='-lm -lutil `pkg-config --libs x11 fontconfig xft`'
}
do_install() {
    make install DESTDIR=${D} PREFIX=/usr TERMINFO=${D}${datadir}/terminfo
}

FILES_${PN} += " \
    ${datadir}/terminfo \
"

inherit update-alternatives

ALTERNATIVE_PRIORITY = "50"

ALTERNATIVE_${PN} = "st st-256color"

ALTERNATIVE_LINK_NAME[st] = "${datadir}/terminfo/s/st"

ALTERNATIVE_LINK_NAME[st-256color] = "${datadir}/terminfo/s/st-256color"
