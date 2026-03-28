SUMMARY = "Lean cryptographic library with PQC-resistant algorithms"
DESCRIPTION = "leancrypto is a cryptographic library that exclusively contains \
PQC-resistant cryptographic algorithms. It is lean, has minimal dependencies, \
supports stack-only operation and provides optimized implementations for \
ML-KEM (Kyber), ML-DSA (Dilithium), SLH-DSA (Sphincs+) and many more"
HOMEPAGE = "https://leancrypto.org"
LICENSE = "BSD-3-Clause | GPL-2.0-only"
LIC_FILES_CHKSUM = " \
    file://LICENSE;md5=4cc9ea7ae2518fb4fb0bbd9d1a60cbf4 \
    file://LICENSE.bsd;md5=66a5cedaf62c4b2637025f049f9b826f \
    file://LICENSE.gplv2;md5=eb723b61539feef013de476e68b5c50a \
    "
SECTION = "libs"
SRC_URI = "git://github.com/smuellerDD/leancrypto.git;branch=master;protocol=https;tag=v${PV} \
           file://leancrypto-tests.sh \
           "
# SRCREV tagged v1.7.0
SRCREV = "e60fba94e8cabf1661a1da488b78b84a4fba56e9"

inherit pkgconfig meson

EXTRA_OEMESON = "-Dstrip=false"
TARGET_LDFLAGS:append = " ${DEBUG_PREFIX_MAP}"

PACKAGECONFIG ??= "secure-exec apps tests"
PACKAGECONFIG[apps] = "-Dapps=enabled,-Dapps=disabled"
PACKAGECONFIG[small-stack] = "-Dsmall_stack=enabled,-Dsmall_stack=disabled"
PACKAGECONFIG[no-asm] = "-Ddisable-asm=true,-Ddisable-asm=false"
PACKAGECONFIG[efi] = "-Defi=enabled,-Defi=disabled"
PACKAGECONFIG[secure-exec] = "-Dsecure_execution=enabled,-Dsecure_execution=disabled"
PACKAGECONFIG[tests] = "-Dtests=enabled,-Dtests=disabled"

do_install:append () {
    if ${@bb.utils.contains('PACKAGECONFIG', 'tests', 'true', 'false', d)}; then
        install -d ${D}${libexecdir}/leancrypto/tests
        for t in $(find ${B} -maxdepth 3 -type f -executable \( -name '*_tester*' -o -name '*_test' \)); do
            basename=$(basename "$t")
            install -m 0755 "$t" ${D}${libexecdir}/leancrypto/tests/leancrypto_${basename}
        done
        install -d ${D}${bindir}
        install -m 0755 ${UNPACKDIR}/leancrypto-tests.sh ${D}${bindir}/leancrypto-tests
    fi
}

PACKAGES =+ "${PN}-tests ${PN}-apps"

RDEPENDS:${PN}-apps += "${PN}"
FILES:${PN}-apps = "${bindir}/lc_* \
                    ${libexecdir}/leancrypto \
                    "
RDEPENDS:${PN}-tests += "${PN}"
FILES:${PN}-tests = "${bindir}/leancrypto-tests \
                    ${libexecdir}/leancrypto/tests \
                    "

BBCLASSEXTEND = "native nativesdk"
