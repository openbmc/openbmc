# Define sources of Trusted Service and all external dependencies

LICENSE = "Apache-2.0 & BSD-3-Clause & BSD-2-Clause & Zlib"

SRC_URI = "git://git.trustedfirmware.org/TS/trusted-services.git;protocol=https;branch=main;name=trusted-services;destsuffix=trusted-services \
"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI:append = "\
    file://0001-Allow-configuring-flash-image-files-compile-time.patch \
    file://0002-Fix-MbedTLS-3.6-and-GCC-14-compatibility.patch \
    file://0001-Relax-pyelftools-dependency.patch \
"

# Trusted Services; aka. v1.1.0
SRCREV_trusted-services = "83bd53b6689ebb071bc13fcd4e01618bab2e74ca"
LIC_FILES_CHKSUM = "file://${S}/license.rst;md5=ea160bac7f690a069c608516b17997f4"

S = "${UNPACKDIR}/trusted-services"
PV ?= "0.0+git"
UPSTREAM_CHECK_COMMITS = "1"

# DTC, tag "v1.6.1"
SRC_URI += "git://github.com/dgibson/dtc;name=dtc;protocol=https;branch=main;destsuffix=dtc"
SRCREV_dtc = "b6910bec11614980a21e46fbccc35934b671bd81"
LIC_FILES_CHKSUM += "file://../dtc/README.license;md5=a1eb22e37f09df5b5511b8a278992d0e"

# MbedTLS, tag "v3.6.0"
SRC_URI += "gitsm://github.com/ARMmbed/mbedtls.git;name=mbedtls;protocol=https;branch=master;destsuffix=mbedtls"
SRCREV_mbedtls = "2ca6c285a0dd3f33982dd57299012dacab1ff206"
LIC_FILES_CHKSUM += "file://../mbedtls/LICENSE;md5=379d5819937a6c2f1ef1630d341e026d"

# Nanopb, tag "nanopb-0.4.8" plus some further fixes
SRC_URI += "git://github.com/nanopb/nanopb.git;name=nanopb;protocol=https;branch=master;destsuffix=nanopb"
SRCREV_nanopb = "6cfe48d6f1593f8fa5c0f90437f5e6522587745e"
LIC_FILES_CHKSUM += "file://../nanopb/LICENSE.txt;md5=9db4b73a55a3994384112efcdb37c01f"

# qcbor, tag "v1.0.0"
SRC_URI += "git://github.com/laurencelundblade/QCBOR.git;name=qcbor;protocol=https;branch=master;destsuffix=qcbor"
SRCREV_qcbor = "56b17bf9f74096774944bcac0829adcd887d391e"
LIC_FILES_CHKSUM += "file://../qcbor/README.md;md5=e8ff2e88a722cdc55eddd0bb9aeca002"

# T_Cose
SRC_URI += "git://github.com/laurencelundblade/t_cose.git;name=tcose;protocol=https;branch=master;destsuffix=tcose"
SRCREV_tcose = "fc3a4b2c7196ff582e8242de8bd4a1bc4eec577f"
LIC_FILES_CHKSUM += "file://../tcose/LICENSE;md5=b2ebdbfb82602b97aa628f64cf4b65ad"

# CppUTest,  tag "v3.8"
SRC_URI += "git://github.com/cpputest/cpputest.git;name=cpputest;protocol=https;branch=master;destsuffix=cpputest"
SRCREV_cpputest = "e25097614e1c4856036366877a02346c4b36bb5b"
LIC_FILES_CHKSUM += "file://../cpputest/COPYING;md5=ce5d5f1fe02bcd1343ced64a06fd4177"

SRCREV_FORMAT = "trusted-services_dtc_mbedtls_nanopb_qcbor_tcose_cpputest"

inherit apply_local_src_patches
LOCAL_SRC_PATCHES_INPUT_DIR = "N/A"

do_apply_local_src_patches() {
    apply_local_src_patches ${S}/external/qcbor ${UNPACKDIR}/qcbor
    apply_local_src_patches ${S}/external/t_cose ${UNPACKDIR}/tcose
    apply_local_src_patches ${S}/external/MbedTLS ${UNPACKDIR}/mbedtls
    apply_local_src_patches ${S}/external/CppUTest ${UNPACKDIR}/cpputest
    apply_local_src_patches ${S}/external/libfdt ${UNPACKDIR}/dtc
    apply_local_src_patches ${S}/external/nanopb ${UNPACKDIR}/nanopb
}

# Paths to dependencies required by some TS SPs/tools
EXTRA_OECMAKE += "-DDTC_SOURCE_DIR=${UNPACKDIR}/dtc \
                  -DCPPUTEST_SOURCE_DIR=${UNPACKDIR}/cpputest \
                  -DNANOPB_SOURCE_DIR=${UNPACKDIR}/nanopb \
                  -DT_COSE_SOURCE_DIR=${UNPACKDIR}/tcose \
                  -DQCBOR_SOURCE_DIR=${UNPACKDIR}/qcbor \
                  -DMBEDTLS_SOURCE_DIR=${UNPACKDIR}/mbedtls \
                 "
