SUMMARY = "Terminus fonts packages (console and X11)"
DESCRIPTION = "Terminus Font is a clean, fixed width bitmap font, designed for \
               long (8 and more hours per day) work with computers."
HOMEPAGE = "http://terminus-font.sourceforge.net/"
AUTHOR = "Dimitar Zhekov"
SECTION = "fonts"

LICENSE = "OFL-1.1"
LIC_FILES_CHKSUM = "file://OFL.TXT;md5=9cadb26f4c5c005618c5ae74f041ec54"

DEPENDS = "hostperl-runtime-native gzip-native"

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}/${BPN}-${PV}.tar.gz"
SRC_URI[md5sum] = "a8e792fe6e84c86ed2b6ed3e2a12ba66"
SRC_URI[sha256sum] = "f6f4876a4dabe6a37c270c20bb9e141e38fb50e0bba200e1b9d0470e5eed97b7"

inherit allarch fontcache

PACKAGECONFIG ?= "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11', '', d)}"
PACKAGECONFIG[x11] = ""

# Don't use font cache mecanism for console packages
FONT_PACKAGES = "${@bb.utils.contains('PACKAGECONFIG', 'x11', '${PN}-pcf', '', d)}"

# Hand made configure script. Don't need oe_runconf
do_configure() {
    chmod +x ${S}/configure
    ${S}/configure --prefix=${prefix} \
                   --psfdir=${datadir}/consolefonts \
                   --acmdir=${datadir}/consoletrans \
                   --x11dir=${datadir}/fonts/terminus
}

do_compile() {
    oe_runmake DESTDIR=${D} psf txt ${@bb.utils.contains('PACKAGECONFIG', 'x11', 'pcf', '', d)}
}

do_install() {
    oe_runmake DESTDIR=${D} install-psf install-acm ${@bb.utils.contains('PACKAGECONFIG', 'x11', 'install-pcf', '', d)}
}

PACKAGES += "${PN}-consolefonts ${PN}-consoletrans ${PN}-pcf"
FILES_${PN}-consolefonts = "${datadir}/consolefonts"
FILES_${PN}-consoletrans = "${datadir}/consoletrans"
FILES_${PN}-pcf = "${datadir}/fonts/terminus"
