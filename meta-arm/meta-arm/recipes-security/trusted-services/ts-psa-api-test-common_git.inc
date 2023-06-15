SUMMARY = "Parts of PSA certification tests (psa-arch-test) for Trusted Services"

TS_ENV = "arm-linux"

require trusted-services.inc

DEPENDS        += "libts"
RDEPENDS:${PN} += "libts"

SRC_URI += "git://github.com/ARM-software/psa-arch-tests.git;name=psatest;protocol=https;branch=main;destsuffix=git/psatest \
            file://0001-Pass-Yocto-build-settings-to-psa-arch-tests-native.patch;patchdir=../psatest \
           "

SRCREV_psatest = "451aa087a40d02c7d04778235014c5619d126471"
LIC_FILES_CHKSUM += "file://../psatest/LICENSE.md;md5=2a944942e1496af1886903d274dedb13"

EXTRA_OECMAKE += "\
                  -DPSA_ARCH_TESTS_SOURCE_DIR=${WORKDIR}/git/psatest \
                 "

# TS check if there are patches to apply to psa-arch-tests, if so apply them
apply_ts_patch() {
    find ${S}/external/psa_arch_tests -type f -name '*.patch' -exec patch -p1 -d ${WORKDIR}/git/psatest -i {} \;
}
do_patch[postfuncs] += "apply_ts_patch"

FILES:${PN} = "${bindir}/${PSA_TEST}"

do_install:append () {
    install -d ${D}${bindir}
    mv ${D}${TS_INSTALL}/bin/${PSA_TEST} ${D}${bindir}

    rm -r --one-file-system ${D}${TS_INSTALL}
}
