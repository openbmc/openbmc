require ttf.inc

SUMMARY = "Adobe Source Code Pro"
HOMEPAGE = "https://github.com/adobe-fonts/source-code-pro"
LICENSE = "OFL-1.1"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=c7c16bdc2c96af797293d68503e5c65c"

inherit allarch fontcache

SRC_URI = " \
    https://github.com/adobe-fonts/source-code-pro/releases/download/2.030R-ro/1.050R-it/source-code-pro-2.030R-ro-1.050R-it.zip \
    file://44-source-code-pro-fonts-fontconfig.conf \
"
SRC_URI[sha256sum] = "da2ac159497d31b0c6d9daa8fc390fb8252e75b4a9805ace6a2c9cccaed4932e"
S = "${WORKDIR}/source-code-pro-2.030R-ro-1.050R-it"

do_install() {
    install -d ${D}${sysconfdir}/fonts/conf.d/
    install -m 0644 ${WORKDIR}/44-source-code-pro-fonts-fontconfig.conf ${D}${sysconfdir}/fonts/conf.d/

    install -d ${D}${datadir}/fonts/truetype/
    find ./ -name '*.otf' -exec install -m 0644 {} ${D}${datadir}/fonts/truetype/ \;
}

FILES:${PN} = " \
    ${sysconfdir}/fonts \
    ${datadir}/fonts \
"

