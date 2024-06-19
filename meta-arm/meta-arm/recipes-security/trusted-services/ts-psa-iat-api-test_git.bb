DESCRIPTION = "Initial Attestation PSA certification tests (psa-arch-test) for Trusted Services"

TS_ENV = "arm-linux"

require ts-psa-api-test-common_${PV}.inc

OECMAKE_SOURCEPATH = "${S}/deployments/psa-api-test/initial_attestation/${TS_ENV}"

PSA_TEST = "psa-iat-api-test"

# psa-arch-tests for INITIAL_ATTESTATION suite can't be built with pre-built qcbor
# Fetch qcbor sources as a temp work-around and pass PSA_TARGET_QCBOR to psa-arch-tests
SRC_URI += "git://github.com/laurencelundblade/QCBOR.git;name=psaqcbor;protocol=https;branch=master;destsuffix=git/psaqcbor \
           "
SRCREV_psaqcbor = "42272e466a8472948bf8fca076d113b81b99f0e0"

EXTRA_OECMAKE += "-DPSA_TARGET_QCBOR=${WORKDIR}/git/psaqcbor \
                 "
# TODO: remove FORTIFY_SOURCE as MbedTLS fails to build in yocto if this
# compilation flag is used.
lcl_maybe_fortify = "${@oe.utils.conditional('OPTLEVEL','-O0','','${OPTLEVEL}',d)}"

# Mbedtls 3.1.0 does not compile with clang.
# This can be removed after TS updated required mbedtls version
TOOLCHAIN = "gcc"
