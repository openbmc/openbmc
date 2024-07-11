SUMMARY = "Parts of PSA certification tests (psa-arch-test) for Trusted Services"

TS_ENV = "arm-linux"

require trusted-services.inc

DEPENDS        += "python3-jsonschema-native python3-jinja2-native"

DEPENDS        += "libts"
RDEPENDS:${PN} += "libts"

SRC_URI += "git://github.com/ARM-software/psa-arch-tests.git;name=psatest;protocol=https;branch=main;destsuffix=git/psatest \
            file://0001-Pass-Yocto-build-settings-to-psa-arch-tests-native.patch;patchdir=../psatest \
           "

SRCREV_psatest = "74dc6646ff594e131a726a5305aba77bac30eceb"
LIC_FILES_CHKSUM += "file://../psatest/LICENSE.md;md5=2a944942e1496af1886903d274dedb13"

EXTRA_OECMAKE += "-DPSA_ARCH_TESTS_SOURCE_DIR=${WORKDIR}/git/psatest"

do_apply_local_src_patches:append() {
    apply_local_src_patches ${S}/external/psa_arch_tests ${WORKDIR}/git/psatest
}

FILES:${PN} = "${bindir}/${PSA_TEST}"

do_install:append () {
    install -d ${D}${bindir}
    mv ${D}${TS_INSTALL}/bin/${PSA_TEST} ${D}${bindir}

    rm -r --one-file-system ${D}${TS_INSTALL}
}
