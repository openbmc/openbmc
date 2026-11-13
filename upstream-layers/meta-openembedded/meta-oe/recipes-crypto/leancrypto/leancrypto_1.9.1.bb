SUMMARY = "Lean cryptographic library with PQC-resistant algorithms"
DESCRIPTION = "leancrypto is a cryptographic library that exclusively contains \
PQC-resistant cryptographic algorithms. It is lean, has minimal dependencies, \
supports stack-only operation and provides optimized implementations for \
ML-KEM (Kyber), ML-DSA (Dilithium), SLH-DSA (Sphincs+) and many more"
SECTION = "libs"

require leancrypto.inc

SRC_URI += "file://leancrypto-tests.sh \
            file://0001-secure_execution_linux-drop-redundant-linux-prctl.h-.patch \
            file://0002-Linker-script-Handle-a-missing-.init-section.patch \
            file://0003-FIPS-checker-builder-Use-OBJCOPY-and-report-failures.patch \
"

inherit pkgconfig meson

CVE_STATUS[CVE-2026-34610] = "fixed-version: fixed since v1.7.1"

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
