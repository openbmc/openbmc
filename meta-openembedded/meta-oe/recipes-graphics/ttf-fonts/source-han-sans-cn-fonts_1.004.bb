require ttf.inc

SUMMARY = "Adobe OpenType Pan-CJK font family for Simplified Chinese"
HOMEPAGE = "https://github.com/adobe-fonts/source-han-sans"
LICENSE = "OFL-1.1"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=55719faa0112708e946b820b24b14097"

inherit allarch fontcache

# Download tends to break - so - or not?
#EXCLUDE_FROM_WORLD = "1"

SRC_URI = " \
    https://github.com/adobe-fonts/source-han-sans/raw/release/SubsetOTF/SourceHanSansCN.zip \
    file://44-source-han-sans-cn.conf \
"
SRC_URI[md5sum] = "d16abc21f6575bb08894efedbed484a2"
SRC_URI[sha256sum] = "0a0e1d8e52833bc352d454d8242da03b82c0efc41323fb66f7435e5b39734a4f"

S = "${WORKDIR}/SourceHanSansCN"

do_install() {
    install -d ${D}${sysconfdir}/fonts/conf.d/
    install -m 0644 ${WORKDIR}/44-source-han-sans-cn.conf ${D}${sysconfdir}/fonts/conf.d/

    install -d ${D}${datadir}/fonts/truetype/
    find ./ -name '*.otf' -exec install -m 0644 {} ${D}${datadir}/fonts/truetype/ \;
}

FILES_${PN} = " \
    ${sysconfdir}/fonts \
    ${datadir}/fonts \
"

