# Define sources of Trusted Service and all external dependencies

LICENSE = "Apache-2.0 & BSD-3-Clause & BSD-2-Clause & Zlib"


FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

# v1.3.0
SRCREV_trusted-services = "a5db25bc3f2892781a07620af5d6625900988281"
LIC_FILES_CHKSUM = "file://${S}/license.rst;md5=ea160bac7f690a069c608516b17997f4"

PV ?= "0.0+git"
UPSTREAM_CHECK_COMMITS = "1"

# DTC, tag "v1.6.1"
SRC_URI_TRUSTED_SERVICES_DTC = "git://github.com/dgibson/dtc;protocol=https;branch=main"
SRCREV_dtc = "b6910bec11614980a21e46fbccc35934b671bd81"
LIC_FILES_CHKSUM += "file://ts-external/dtc/README.license;md5=a1eb22e37f09df5b5511b8a278992d0e"

# MbedTLS, tag "v3.6.5"
SRC_URI_TRUSTED_SERVICES_MBEDTLS = "gitsm://github.com/Mbed-TLS/mbedtls;protocol=https;branch=master"
SRCREV_mbedtls = "e185d7fd85499c8ce5ca2a54f5cf8fe7dbe3f8df"
LIC_FILES_CHKSUM += "file://ts-external/mbedtls/LICENSE;md5=379d5819937a6c2f1ef1630d341e026d"

# Nanopb, tag "nanopb-0.4.9.1"
SRC_URI_TRUSTED_SERVICES_NANOPB = "git://github.com/nanopb/nanopb.git;protocol=https;branch=maintenance_0.4"
SRCREV_nanopb = "cad3c18ef15a663e30e3e43e3a752b66378adec1"
LIC_FILES_CHKSUM += "file://ts-external/nanopb/LICENSE.txt;md5=9db4b73a55a3994384112efcdb37c01f"

# qcbor, tag "v1.0.0"
SRC_URI_TRUSTED_SERVICES_QCBOR = "git://github.com/laurencelundblade/QCBOR.git;protocol=https;branch=master"
SRCREV_qcbor = "56b17bf9f74096774944bcac0829adcd887d391e"
LIC_FILES_CHKSUM += "file://ts-external/qcbor/README.md;md5=e8ff2e88a722cdc55eddd0bb9aeca002"

# T_Cose
SRC_URI_TRUSTED_SERVICES_TCOSE = "git://github.com/laurencelundblade/t_cose.git;protocol=https;branch=master"
SRCREV_tcose = "fc3a4b2c7196ff582e8242de8bd4a1bc4eec577f"
LIC_FILES_CHKSUM += "file://ts-external/tcose/LICENSE;md5=b2ebdbfb82602b97aa628f64cf4b65ad"

# CppUTest,  tag "v3.8"
SRC_URI_TRUSTED_SERVICES_CPPUTEST = "git://github.com/cpputest/cpputest.git;protocol=https;branch=master"
SRCREV_cpputest = "e25097614e1c4856036366877a02346c4b36bb5b"
LIC_FILES_CHKSUM += "file://ts-external/CppUTest/COPYING;md5=ce5d5f1fe02bcd1343ced64a06fd4177"

SRC_URI = "git://git.trustedfirmware.org/TS/trusted-services.git;protocol=https;branch=main;name=trusted-services;destsuffix=ts; \
           ${SRC_URI_TRUSTED_SERVICES_DTC};name=dtc;destsuffix=ts/ts-external/dtc \
           ${SRC_URI_TRUSTED_SERVICES_MBEDTLS};name=mbedtls;destsuffix=ts/ts-external/mbedtls \
           ${SRC_URI_TRUSTED_SERVICES_NANOPB};name=nanopb;destsuffix=ts/ts-external/nanopb \
           ${SRC_URI_TRUSTED_SERVICES_QCBOR};name=qcbor;destsuffix=ts/ts-external/qcbor \
           ${SRC_URI_TRUSTED_SERVICES_TCOSE};name=tcose;destsuffix=ts/ts-external/tcose \
           ${SRC_URI_TRUSTED_SERVICES_CPPUTEST};branch=master;name=cpputest;destsuffix=ts/ts-external/CppUTest \
           file://0001-Allow-configuring-flash-image-files-compile-time.patch \
           file://0002-Fix-MbedTLS-3.6-and-GCC-14-compatibility.patch \
           file://0001-Relax-pyelftools-dependency.patch \
           file://0001-fTPM-add-go_idle-support.patch \
"
S =        "${UNPACKDIR}/ts"

inherit apply_local_src_patches
LOCAL_SRC_PATCHES_INPUT_DIR = "N/A"

do_apply_local_src_patches() {
    apply_local_src_patches ${S}/external/qcbor ${S}/ts-external/qcbor
    apply_local_src_patches ${S}/external/t_cose ${S}/ts-external/tcose
    apply_local_src_patches ${S}/external/MbedTLS ${S}/ts-external/mbedtls
    apply_local_src_patches ${S}/external/CppUTest ${S}/ts-external/CppUTest
    apply_local_src_patches ${S}/external/libfdt ${S}/ts-external/dtc
    apply_local_src_patches ${S}/external/nanopb ${S}/ts-external/nanopb
}

SRCREV_FORMAT = "trusted-services_dtc_mbedtls_nanopb_qcbor_tcose_cpputest"

# Paths to dependencies required by some TS SPs/tools
EXTRA_OECMAKE += "-DDTC_SOURCE_DIR=${S}/ts-external/dtc \
                  -DMBEDTLS_SOURCE_DIR=${S}/ts-external/mbedtls \
                  -DNANOPB_SOURCE_DIR=${S}/ts-external/nanopb \
                  -DQCBOR_SOURCE_DIR=${S}/ts-external/qcbor \
                  -DT_COSE_SOURCE_DIR=${S}/ts-external/tcose \
                  -DCPPUTEST_SOURCE_DIR=${S}/ts-external/CppUTest \
                 "
