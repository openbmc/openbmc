# Define sources of Trusted Service and all external dependencies

LICENSE = "Apache-2.0 & BSD-3-Clause & BSD-2-Clause & Zlib"

SRC_URI = "git://git.trustedfirmware.org/TS/trusted-services.git;protocol=https;branch=integration;name=trusted-services;destsuffix=git/trusted-services \
"

#latest on 12.10.22.
SRCREV_trusted-services = "3d4956770f89eb9ae0a73257901ae6277c078da6"
LIC_FILES_CHKSUM = "file://${S}/license.rst;md5=ea160bac7f690a069c608516b17997f4"

S = "${WORKDIR}/git/trusted-services"
PV ?= "0.0+git${SRCPV}"

# DTC, tag "v1.6.1"
SRC_URI += "git://github.com/dgibson/dtc;name=dtc;protocol=https;branch=main;destsuffix=git/dtc"
SRCREV_dtc = "b6910bec11614980a21e46fbccc35934b671bd81"
LIC_FILES_CHKSUM += "file://../dtc/README.license;md5=a1eb22e37f09df5b5511b8a278992d0e"

# MbedTLS, tag "mbedtls-3.1.0"
SRC_URI += "git://github.com/ARMmbed/mbedtls.git;name=mbedtls;protocol=https;branch=master;destsuffix=git/mbedtls"
SRCREV_mbedtls = "d65aeb37349ad1a50e0f6c9b694d4b5290d60e49"
LIC_FILES_CHKSUM += "file://../mbedtls/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

# Nanopb, tag "nanopb-0.4.6"
SRC_URI += "git://github.com/nanopb/nanopb.git;name=nanopb;protocol=https;branch=master;destsuffix=git/nanopb"
SRCREV_nanopb = "afc499f9a410fc9bbf6c9c48cdd8d8b199d49eb4"
LIC_FILES_CHKSUM += "file://../nanopb/LICENSE.txt;md5=9db4b73a55a3994384112efcdb37c01f"

# qcbor, tag "v1.0.0"
SRC_URI += "git://github.com/laurencelundblade/QCBOR.git;name=qcbor;protocol=https;branch=master;destsuffix=git/qcbor"
SRCREV_qcbor = "56b17bf9f74096774944bcac0829adcd887d391e"
LIC_FILES_CHKSUM += "file://../qcbor/README.md;md5=e8ff2e88a722cdc55eddd0bb9aeca002"

# T_Cose
SRC_URI += "git://github.com/laurencelundblade/t_cose.git;name=tcose;protocol=https;branch=master;destsuffix=git/tcose"
SRCREV_tcose = "fc3a4b2c7196ff582e8242de8bd4a1bc4eec577f"
LIC_FILES_CHKSUM += "file://../tcose/LICENSE;md5=b2ebdbfb82602b97aa628f64cf4b65ad"

# CppUTest,  tag "v3.8"
SRC_URI += "git://github.com/cpputest/cpputest.git;name=cpputest;protocol=https;branch=master;destsuffix=git/cpputest"
SRCREV_cpputest = "e25097614e1c4856036366877a02346c4b36bb5b"
LIC_FILES_CHKSUM += "file://../cpputest/COPYING;md5=ce5d5f1fe02bcd1343ced64a06fd4177"

# Libmetal
SRC_URI += "git://github.com/OpenAMP/libmetal.git;name=libmetal;protocol=https;branch=main;destsuffix=git/libmetal"
SRCREV_libmetal = "f252f0e007fbfb8b3a52b1d5901250ddac96baad"
LIC_FILES_CHKSUM += "file://../libmetal/LICENSE.md;md5=fe0b8a4beea8f0813b606d15a3df3d3c"

# Openamp
SRC_URI += "git://github.com/OpenAMP/open-amp.git;name=openamp;protocol=https;branch=main;destsuffix=git/openamp"
SRCREV_openamp = "347397decaa43372fc4d00f965640ebde042966d"
LIC_FILES_CHKSUM += "file://../openamp/LICENSE.md;md5=a8d8cf662ef6bf9936a1e1413585ecbf"

# TS ships patches for external dependencies that needs to be applied
apply_ts_patches() {
    for p in ${S}/external/qcbor/*.patch; do
        patch -p1 -N -d ${WORKDIR}/git/qcbor < ${p} || true
    done
    for p in ${S}/external/t_cose/*.patch; do
        patch -p1 -N -d ${WORKDIR}/git/tcose < ${p} || true
    done
    for p in ${S}/external/CppUTest/*.patch; do
        patch -p1 -d ${WORKDIR}/git/cpputest < ${p}
    done
}
do_patch[postfuncs] += "apply_ts_patches"

# Paths to dependencies required by some TS SPs/tools
EXTRA_OECMAKE += "-DDTC_SOURCE_DIR=${WORKDIR}/git/dtc \
                  -DCPPUTEST_SOURCE_DIR=${WORKDIR}/git/cpputest \
                  -DNANOPB_SOURCE_DIR=${WORKDIR}/git/nanopb \
                  -DT_COSE_SOURCE_DIR=${WORKDIR}/git/tcose \
                  -DQCBOR_SOURCE_DIR=${WORKDIR}/git/qcbor \
                  -DMBEDTLS_SOURCE_DIR=${WORKDIR}/git/mbedtls \
                  -DOPENAMP_SOURCE_DIR=${WORKDIR}/git/openamp \
                  -DLIBMETAL_SOURCE_DIR=${WORKDIR}/git/libmetal \
                 "
