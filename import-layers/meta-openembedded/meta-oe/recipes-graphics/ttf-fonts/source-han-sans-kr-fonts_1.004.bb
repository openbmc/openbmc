require ttf.inc

SUMMARY = "Adobe OpenType Pan-CJK font family for Korean"
HOMEPAGE = "https://github.com/adobe-fonts/source-han-sans"
LICENSE = "OFL-1.1"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=55719faa0112708e946b820b24b14097"

inherit allarch fontcache

# Download tends to break - so - or not?
#EXCLUDE_FROM_WORLD = "1"

SRC_URI = " \
    https://github.com/adobe-fonts/source-han-sans/raw/release/SubsetOTF/SourceHanSansKR.zip \
    file://44-source-han-sans-kr.conf \
"
SRC_URI[md5sum] = "f8d1bd6c87d8575afdb25e2f46bd81d4"
SRC_URI[sha256sum] = "38fd15c80f9980492faaa1af39ff873d8a38e45027023fb17d5b10d4b4b0e6af"

S = "${WORKDIR}/SourceHanSansKR"

do_install() {
    install -d ${D}${sysconfdir}/fonts/conf.d/
    install -m 0644 ${WORKDIR}/44-source-han-sans-kr.conf ${D}${sysconfdir}/fonts/conf.d/

    install -d ${D}${datadir}/fonts/truetype/
    find ./ -name '*.otf' -exec install -m 0644 {} ${D}${datadir}/fonts/truetype/ \;
}

FILES_${PN} = " \
    ${sysconfdir}/fonts \
    ${datadir}/fonts \
"

