SUMMARY = "Terminus fonts packages (console and X11)"
DESCRIPTION = "Terminus Font is a clean, fixed width bitmap font, designed for \
               long (8 and more hours per day) work with computers."
HOMEPAGE = "http://terminus-font.sourceforge.net/"
AUTHOR = "Dimitar Zhekov"
SECTION = "fonts"

LICENSE = "OFL-1.1"
LIC_FILES_CHKSUM = "file://OFL.TXT;md5=9b226721636fde0db38ea656c2aae4bf"

DEPENDS = "hostperl-runtime-native gzip-native bdftopcf-native"

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}/${BPN}-${PV}.tar.gz"
SRC_URI[md5sum] = "a2cb88f6cf174f3d07de93b91e115a8d"
SRC_URI[sha256sum] = "34799c8dd5cec7db8016b4a615820dfb43b395575afbb24fc17ee19c869c94af"

inherit allarch fontcache

PACKAGECONFIG ?= "${@bb.utils.filter('DISTRO_FEATURES', 'x11', d)}"
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
    oe_runmake DESTDIR=${D} psf ${@bb.utils.contains('PACKAGECONFIG', 'x11', 'pcf', '', d)}
}

do_install() {
    oe_runmake DESTDIR=${D} install-psf ${@bb.utils.contains('PACKAGECONFIG', 'x11', 'install-pcf', '', d)}
}

PACKAGES += "${PN}-consolefonts ${PN}-consoletrans ${PN}-pcf"
FILES_${PN}-consolefonts = "${datadir}/consolefonts"
FILES_${PN}-consoletrans = "${datadir}/consoletrans"
FILES_${PN}-pcf = "${datadir}/fonts/terminus"
