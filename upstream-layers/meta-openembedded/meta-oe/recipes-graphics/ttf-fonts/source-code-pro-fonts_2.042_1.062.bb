require ttf.inc

SUMMARY = "Adobe Source Code Pro"
HOMEPAGE = "https://github.com/adobe-fonts/source-code-pro"
LICENSE = "OFL-1.1"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/${LICENSE};md5=fac3a519e5e9eb96316656e0ca4f2b90"

inherit allarch fontcache

SRC_URI = " \
    https://github.com/adobe-fonts/source-code-pro/releases/download/2.042R-u/1.062R-i/1.026R-vf/OTF-source-code-pro-2.042R-u_1.062R-i.zip;subdir=${BP} \
    file://44-source-code-pro-fonts-fontconfig.conf \
"
SRC_URI[sha256sum] = "754a2e3ebb945ae905d720ac5896b3b34acc9546dd6551ef9536869788629dae"
S = "${UNPACKDIR}/${BP}"

UPSTREAM_CHECK_URI = "https://github.com/adobe-fonts/source-code-pro/tags"
UPSTREAM_CHECK_REGEX = "releases/tag/(?P<pver>\d+\.\d+)R-u"

do_install() {
    install -d ${D}${sysconfdir}/fonts/conf.d/
    install -m 0644 ${UNPACKDIR}/44-source-code-pro-fonts-fontconfig.conf ${D}${sysconfdir}/fonts/conf.d/

    install -d ${D}${datadir}/fonts/truetype/
    find ./ -name '*.otf' -exec install -m 0644 {} ${D}${datadir}/fonts/truetype/ \;
}

FILES:${PN} = " \
    ${sysconfdir}/fonts \
    ${datadir}/fonts \
"

