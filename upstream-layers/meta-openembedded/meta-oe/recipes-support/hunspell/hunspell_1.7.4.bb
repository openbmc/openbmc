SUMMARY = "A spell checker and morphological analyzer library"
HOMEPAGE = "http://hunspell.github.io/"
LICENSE = "GPL-2.0-only OR LGPL-2.1-only"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=75859989545e37968a99b631ef42722e \
    file://COPYING.LESSER;md5=c96ca6c1de8adc025adfada81d06fba5 \
"

SRCREV = "92aa34b3f378ec115562fb06bd1728d566a339ec"
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
    for ext in dic aff good wrong sug morph root trace; do
        find ${S}/tests -maxdepth 1 -name "*.$ext" -exec cp {} ${D}${PTEST_PATH}/tests/ \;
    done

    # Patch test.sh to use installed binaries
    sed -i 's|HUNSPELL="$(dirname $0)"/../src/tools/hunspell|HUNSPELL="${bindir}/hunspell"|' ${D}${PTEST_PATH}/tests/test.sh
    sed -i 's|ANALYZE="$(dirname $0)"/../src/tools/analyze|ANALYZE="${bindir}/analyze"|' ${D}${PTEST_PATH}/tests/test.sh
    sed -i 's|alias hunspell=.*HUNSPELL.*|alias hunspell="$HUNSPELL"|' ${D}${PTEST_PATH}/tests/test.sh
    sed -i 's|alias analyze=.*ANALYZE.*|alias analyze="$ANALYZE"|' ${D}${PTEST_PATH}/tests/test.sh
}

RDEPENDS:${PN}-ptest += "bash"

RDEPENDS:${PN}-ptest:append:libc-glibc = " \
    glibc-gconv-iso8859-1 \
    glibc-gconv-iso8859-2 \
    glibc-gconv-iso8859-15 \
"

BBCLASSEXTEND = "native"
