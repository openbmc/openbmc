require ttf.inc

SUMMARY = "Adobe OpenType Pan-CJK font family for Japanese"
HOMEPAGE = "https://github.com/adobe-fonts/source-han-sans"
LICENSE = "OFL-1.1"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/${LICENSE};md5=fac3a519e5e9eb96316656e0ca4f2b90"

inherit allarch fontcache

# Download tends to break - so - or not?
#EXCLUDE_FROM_WORLD = "1"

SRC_URI = " \
    svn://github.com/adobe-fonts/source-han-sans;module=branches/release/SubsetOTF/JP;protocol=http;rev=82 \
    file://44-source-han-sans-jp.conf \
"
SRC_URI[md5sum] = "908fbf97f3df04a6838708c093f1e900"
SRC_URI[sha256sum] = "dc6dbae3fba35f220bac88ba7130b826c7efe1282f472788fae3628b79be3f54"

S = "${WORKDIR}/SourceHanSansJP"

do_install() {
    install -d ${D}${sysconfdir}/fonts/conf.d/
    install -m 0644 ${WORKDIR}/44-source-han-sans-jp.conf ${D}${sysconfdir}/fonts/conf.d/

    install -d ${D}${datadir}/fonts/truetype/
    find ./ -name '*.otf' -exec install -m 0644 {} ${D}${datadir}/fonts/truetype/ \;
}

FILES:${PN} = " \
    ${sysconfdir}/fonts \
    ${datadir}/fonts \
"

