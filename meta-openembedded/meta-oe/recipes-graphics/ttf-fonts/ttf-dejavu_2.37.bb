require ttf.inc

SUMMARY = "DejaVu font - TTF Edition"
HOMEPAGE = "http://dejavu.sourceforge.net/wiki/"
LICENSE = "BitstreamVera"
LIC_FILES_CHKSUM = "file://../LICENSE;md5=449b2c30bfe5fa897fe87b8b70b16cfa"

# all subpackages except ${PN}-common itself rdepends on ${PN}-common
RDEPENDS:${PN}-sans = "${PN}-common"
RDEPENDS:${PN}-sans-mono = "${PN}-common"
RDEPENDS:${PN}-sans-condensed = "${PN}-common"
RDEPENDS:${PN}-serif = "${PN}-common"
RDEPENDS:${PN}-serif-condensed = "${PN}-common"
RDEPENDS:${PN}-mathtexgyre = "${PN}-common"
RDEPENDS:${PN}-common = ""
PR = "r7"

SRC_URI = "${SOURCEFORGE_MIRROR}/dejavu/dejavu-fonts-ttf-${PV}.tar.bz2 \
           file://30-dejavu-aliases.conf"

S = "${WORKDIR}/dejavu-fonts-ttf-${PV}/ttf"

do_install:append () {
    install -d ${D}${sysconfdir}/fonts/conf.d/
    install -m 0644 ${WORKDIR}/30-dejavu-aliases.conf ${D}${sysconfdir}/fonts/conf.d/
}

PACKAGES = "\
            ${PN}-sans \
            ${PN}-sans-mono \
            ${PN}-sans-condensed \
            ${PN}-serif \
            ${PN}-serif-condensed \
            ${PN}-mathtexgyre \
            ${PN}-common"
FONT_PACKAGES = "${PN}-sans ${PN}-sans-mono ${PN}-sans-condensed ${PN}-serif ${PN}-serif-condensed ${PN}-mathtexgyre"

FILES:${PN}-sans            = "${datadir}/fonts/truetype/DejaVuSans.ttf ${datadir}/fonts/truetype/DejaVuSans-*.ttf"
FILES:${PN}-sans-mono       = "${datadir}/fonts/truetype/DejaVuSansMono*.ttf"
FILES:${PN}-sans-condensed  = "${datadir}/fonts/truetype/DejaVuSansCondensed*.ttf"
FILES:${PN}-serif           = "${datadir}/fonts/truetype/DejaVuSerif.ttf ${datadir}/fonts/truetype/DejaVuSerif-*.ttf"
FILES:${PN}-serif-condensed = "${datadir}/fonts/truetype/DejaVuSerifCondensed*.ttf"
FILES:${PN}-mathtexgyre     = "${datadir}/fonts/truetype/DejaVuMathTeXGyre.ttf"
FILES:${PN}-common          = "${sysconfdir}"

SRC_URI[md5sum] = "d0efec10b9f110a32e9b8f796e21782c"
SRC_URI[sha256sum] = "fa9ca4d13871dd122f61258a80d01751d603b4d3ee14095d65453b4e846e17d7"

BBCLASSEXTEND = "native nativesdk"

# Allow installation of fonts into recipe-sysroot-native
SYSROOT_DIRS_BLACKLIST:remove = "${datadir}/fonts"

