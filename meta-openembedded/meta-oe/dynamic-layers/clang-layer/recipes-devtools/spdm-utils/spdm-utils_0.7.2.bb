SUMMARY = "Linux applications to manage, test and develop devices supporting DMTF Security Protocol and Data Model (SPDM) "
HOMEPAGE = "https://github.com/westerndigitalcorporation/spdm-utils"
LICENSE = "Apache-2.0 & MIT"
LIC_FILES_CHKSUM = "\
    file://LICENSE-MIT;md5=b377b220f43d747efdec40d69fcaa69d \
    file://LICENSE-APACHE;md5=22a53954e4e0ec258dfce4391e905dac \
    "

inherit cargo cargo-update-recipe-crates pkgconfig

export BINDGEN_EXTRA_CLANG_ARGS
BINDGEN_EXTRA_CLANG_ARGS = "--sysroot=${WORKDIR}/recipe-sysroot -I${WORKDIR}/recipe-sysroot/usr/include"

SRC_URI += "git://github.com/westerndigitalcorporation/spdm-utils.git;protocol=https;branch=master"

include spdm-utils-crates.inc

SRCREV = "f67ac9e00b79f603ecbbd29928a4ecc3dec5abd5"
S = "${WORKDIR}/git"

# bindgen-cli comes from meta-clang and depends on libclang
DEPENDS += "libspdm udev bindgen-cli-native pciutils"

COMPATIBLE_HOST:riscv32 = "null"
COMPATIBLE_HOST:mipsarcho32 = "null"
