SUMMARY = "Google Fira Fonts- TTF Edition"
HOMEPAGE = "https://fonts.google.com/?query=fira"
LICENSE = "OFL-1.1"
LIC_FILES_CHKSUM = " \
    file://${S}/firamono/OFL.txt;md5=0373cf792d4b95c61399b94c02702892 \
    file://${S}/firacode/OFL.txt;md5=c75ca55aa0a5809a854d87db2a8ebf23 \
    file://${S}/firasans/OFL.txt;md5=de28deb2f8c1f23fd4d6918113ac3ddd \
    file://${S}/firasanscondensed/OFL.txt;md5=de28deb2f8c1f23fd4d6918113ac3ddd \
    file://${S}/firasansextracondensed/OFL.txt;md5=de28deb2f8c1f23fd4d6918113ac3ddd \
"

SRCREV_FORMAT = "firamono_firacode_firasans_firasanscondensed_firasansextracondensed"

SRCREV_firamono = "701bd391b1a4b3238de193a8523009ecef1be42c"
SRCREV_firacode = "701bd391b1a4b3238de193a8523009ecef1be42c"
SRCREV_firasans = "701bd391b1a4b3238de193a8523009ecef1be42c"
SRCREV_firasanscondensed = "701bd391b1a4b3238de193a8523009ecef1be42c"
SRCREV_firasansextracondensed = "701bd391b1a4b3238de193a8523009ecef1be42c"

SRC_URI = "git://github.com/google/fonts.git;protocol=https;branch=main;subpath=ofl/firamono;name=firamono \
           git://github.com/google/fonts.git;protocol=https;branch=main;subpath=ofl/firacode;name=firacode \
           git://github.com/google/fonts.git;protocol=https;branch=main;subpath=ofl/firasans;name=firasans \
           git://github.com/google/fonts.git;protocol=https;branch=main;subpath=ofl/firasanscondensed;name=firasanscondensed \
           git://github.com/google/fonts.git;protocol=https;branch=main;subpath=ofl/firasansextracondensed;name=firasansextracondensed"

S = "${WORKDIR}"

do_install:append() {
    install -d ${D}${datadir}/fonts/truetype/
    find ${S} -path 'fira*/*.tt[cf]' -exec install -m 0644 {} ${D}${datadir}/fonts/truetype/{} \;
    install -D -m 0644 ${S}/firamono/OFL.txt ${D}${datadir}/licenses/${PN}mono/OFL.txt
    install -D -m 0644 ${S}/firacode/OFL.txt ${D}${datadir}/licenses/${PN}code/OFL.txt
    install -D -m 0644 ${S}/firasans/OFL.txt ${D}${datadir}/licenses/${PN}sans/OFL.txt
    install -D -m 0644 ${S}/firasanscondensed/OFL.txt ${D}${datadir}/licenses/${PN}sanscondensed/OFL.txt
    install -D -m 0644 ${S}/firasansextracondensed/OFL.txt ${D}${datadir}/licenses/${PN}sansextracondensed/OFL.txt
}

PACKAGES =+ "${PN}-mono ${PN}-code ${PN}-sans ${PN}-sanscondensed ${PN}-sansextracondensed"

FILES:${PN}-mono += " \
                    ${datadir}/fonts/truetype/FiraMono* \
                    ${datadir}/licenses/${PN}mono/OFL.txt \
                "
FILES:${PN}-code += " \
                    ${datadir}/fonts/truetype/FiraCode* \
                    ${datadir}/licenses/${PN}code/OFL.txt \
                "
FILES:${PN}-sans += " \
                    ${datadir}/fonts/truetype/FiraSans-* \
                    ${datadir}/licenses/${PN}sans/OFL.txt \
                "
FILES:${PN}-sanscondensed += " \
                            ${datadir}/fonts/truetype/FiraSansCondensed-* \
                            ${datadir}/licenses/${PN}sanscondensed/OFL.txt \
                        "
FILES:${PN}-sansextracondensed += " \
                                    ${datadir}/fonts/truetype/FiraSansExtraCondensed-* \
                                    ${datadir}/licenses/${PN}sansextracondensed/OFL.txt \
                                 "

require ttf.inc
