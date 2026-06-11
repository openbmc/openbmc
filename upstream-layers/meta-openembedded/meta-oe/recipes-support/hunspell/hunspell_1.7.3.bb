SUMMARY = "A spell checker and morphological analyzer library"
HOMEPAGE = "http://hunspell.github.io/"
LICENSE = "GPL-2.0-only | LGPL-2.1-only"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=75859989545e37968a99b631ef42722e \
    file://COPYING.LESSER;md5=c96ca6c1de8adc025adfada81d06fba5 \
"

SRCREV = "c5f98152a274e25b5107101104bef632b83a0cc9"
SRC_URI = "git://github.com/${BPN}/${BPN}.git;branch=master;protocol=https;tag=v${PV} \
           file://run-ptest \
"

inherit autotools pkgconfig gettext ptest

# ispellaff2myspell: A program to convert ispell affix tables to myspell format
PACKAGES =+ "${PN}-ispell"
FILES:${PN}-ispell = "${bindir}/ispellaff2myspell"
RDEPENDS:${PN}-ispell = "perl"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    install -m 0755 ${S}/tests/test.sh ${D}${PTEST_PATH}/tests/
    
    # Install test data files
    for ext in dic aff good wrong sug morph root; do
        find ${S}/tests -maxdepth 1 -name "*.$ext" -exec cp {} ${D}${PTEST_PATH}/tests/ \;
    done
    
    # Patch test.sh to use installed binaries
    sed -i 's|HUNSPELL="$(dirname $0)"/../src/tools/hunspell|HUNSPELL="${bindir}/hunspell"|' ${D}${PTEST_PATH}/tests/test.sh
    sed -i 's|ANALYZE="$(dirname $0)"/../src/tools/analyze|ANALYZE="${bindir}/analyze"|' ${D}${PTEST_PATH}/tests/test.sh
    sed -i 's|alias hunspell=.*HUNSPELL.*|alias hunspell="$HUNSPELL"|' ${D}${PTEST_PATH}/tests/test.sh
    sed -i 's|alias analyze=.*ANALYZE.*|alias analyze="$ANALYZE"|' ${D}${PTEST_PATH}/tests/test.sh
}

RDEPENDS:${PN}-ptest += "bash glibc-gconv-iso8859-2"

BBCLASSEXTEND = "native"
