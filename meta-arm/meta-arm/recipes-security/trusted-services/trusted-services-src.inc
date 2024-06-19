# Define sources of Trusted Service and all external dependencies

LICENSE = "Apache-2.0 & BSD-3-Clause & BSD-2-Clause & Zlib"

SRC_URI = "git://git.trustedfirmware.org/TS/trusted-services.git;protocol=https;branch=main;name=trusted-services;destsuffix=git/trusted-services \
"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append = "\
    file://0001-Allow-configuring-flash-image-files-compile-time.patch \
"

# Trusted Services; aka. 2024 April 19
SRCREV_trusted-services = "602be607198ea784bc5ab1c0c9d3ac4e2c67f1d9"
LIC_FILES_CHKSUM = "file://${S}/license.rst;md5=ea160bac7f690a069c608516b17997f4"

S = "${WORKDIR}/git/trusted-services"
PV ?= "0.0+git"

# DTC, tag "v1.6.1"
SRC_URI += "git://github.com/dgibson/dtc;name=dtc;protocol=https;branch=main;destsuffix=git/dtc"
SRCREV_dtc = "b6910bec11614980a21e46fbccc35934b671bd81"
LIC_FILES_CHKSUM += "file://../dtc/README.license;md5=a1eb22e37f09df5b5511b8a278992d0e"

# MbedTLS, tag "v3.5.1"
SRC_URI += "git://github.com/ARMmbed/mbedtls.git;name=mbedtls;protocol=https;branch=master;destsuffix=git/mbedtls"
SRCREV_mbedtls = "15254759342494c7e969766d5424d78d7deb9bfa"
LIC_FILES_CHKSUM += "file://../mbedtls/LICENSE;md5=379d5819937a6c2f1ef1630d341e026d"

# Nanopb, tag "nanopb-0.4.7" plus some further fixes
SRC_URI += "git://github.com/nanopb/nanopb.git;name=nanopb;protocol=https;branch=master;destsuffix=git/nanopb"
SRCREV_nanopb = "dbbf5d8992295aae669b8071eadad02f87d5faf0"
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

SRCREV_FORMAT = "trusted-services_dtc_mbedtls_nanopb_qcbor_tcose_cpputest"

inherit apply_local_src_patches
LOCAL_SRC_PATCHES_INPUT_DIR = "N/A"

do_apply_local_src_patches() {
    apply_local_src_patches ${S}/external/qcbor ${WORKDIR}/git/qcbor
    apply_local_src_patches ${S}/external/t_cose ${WORKDIR}/git/tcose
    apply_local_src_patches ${S}/external/MbedTLS ${WORKDIR}/git/mbedtls
    apply_local_src_patches ${S}/external/CppUTest ${WORKDIR}/git/cpputest
    apply_local_src_patches ${S}/external/libfdt ${WORKDIR}/git/dtc
    apply_local_src_patches ${S}/external/nanopb ${WORKDIR}/git/nanopb
}

do_config:append:() {
    # Fine tune MbedTLS configuration for crypto only operation.
    sh -c "cd ${WORKDIR}/git/mbedtls; python3 scripts/config.py crypto"
}

# Paths to dependencies required by some TS SPs/tools
EXTRA_OECMAKE += "-DDTC_SOURCE_DIR=${WORKDIR}/git/dtc \
                  -DCPPUTEST_SOURCE_DIR=${WORKDIR}/git/cpputest \
                  -DNANOPB_SOURCE_DIR=${WORKDIR}/git/nanopb \
                  -DT_COSE_SOURCE_DIR=${WORKDIR}/git/tcose \
                  -DQCBOR_SOURCE_DIR=${WORKDIR}/git/qcbor \
                  -DMBEDTLS_SOURCE_DIR=${WORKDIR}/git/mbedtls \
                 "
