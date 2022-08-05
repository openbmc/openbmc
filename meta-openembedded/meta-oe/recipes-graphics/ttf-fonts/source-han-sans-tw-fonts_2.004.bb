require ttf.inc

SUMMARY = "Adobe OpenType Pan-CJK font family for Traditional Chinese"
HOMEPAGE = "https://github.com/adobe-fonts/source-han-sans"
LICENSE = "OFL-1.1"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/${LICENSE};md5=fac3a519e5e9eb96316656e0ca4f2b90"

inherit allarch fontcache

# Download tends to break - so - or not?
#EXCLUDE_FROM_WORLD = "1"

SRC_URI = " \
    svn://github.com/adobe-fonts/source-han-sans;module=branches/release/SubsetOTF/TW;protocol=http;rev=82 \
    file://44-source-han-sans-tw.conf \
"
SRC_URI[md5sum] = "6533b71b31c19e548768f0fc963202f3"
SRC_URI[sha256sum] = "92ba161921c5cdec5a8f8d5711676f0865b50cee071c25eb4bd4125b5af59fd0"

S = "${WORKDIR}/SourceHanSansTW"

do_install() {
    install -d ${D}${sysconfdir}/fonts/conf.d/
    install -m 0644 ${WORKDIR}/44-source-han-sans-tw.conf ${D}${sysconfdir}/fonts/conf.d/

    install -d ${D}${datadir}/fonts/truetype/
    find ./ -name '*.otf' -exec install -m 0644 {} ${D}${datadir}/fonts/truetype/ \;
}

FILES:${PN} = " \
    ${sysconfdir}/fonts \
    ${datadir}/fonts \
"

