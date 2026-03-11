SUMMARY = "Terminus fonts packages (console and X11)"
DESCRIPTION = "Terminus Font is a clean, fixed width bitmap font, designed for \
               long (8 and more hours per day) work with computers."
HOMEPAGE = "http://terminus-font.sourceforge.net/"
SECTION = "fonts"

LICENSE = "OFL-1.1"
LIC_FILES_CHKSUM = "file://OFL.TXT;md5=f57e6cca943dbc6ef83dc14f1855bdcc"

DEPENDS = "hostperl-runtime-native gzip-native bdftopcf-native"

SRC_URI = "${SOURCEFORGE_MIRROR}/${BPN}/${BPN}-${PV}.tar.gz \
          file://use-no-name-option-for-gzip.patch \
          "
SRC_URI[sha256sum] = "d961c1b781627bf417f9b340693d64fc219e0113ad3a3af1a3424c7aa373ef79"

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
FILES:${PN}-consolefonts = "${datadir}/consolefonts"
FILES:${PN}-consoletrans = "${datadir}/consoletrans"
FILES:${PN}-pcf = "${datadir}/fonts/terminus"
