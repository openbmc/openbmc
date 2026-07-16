SUMMARY = "The fastest and safest AV1 encoder"
HOMEPAGE = "https://github.com/xiph/rav1e"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6a960f542b01a3e538903e87236b3668"

inherit cargo_c pkgconfig cargo-update-recipe-crates

require ${PN}-crates.inc

DEPENDS:append:x86 = " nasm-native"
DEPENDS:append:x86-64 = " nasm-native"

SRC_URI += "git://github.com/xiph/rav1e.git;protocol=https;nobranch=1;tag=${PV}"
SRCREV = "a2f01b3e233f531c28a20b4c29fb5c9e5d29fa6d"

# TODO we need to pass -fdebug-prefix-map to the nasm calls
INHIBIT_PACKAGE_DEBUG_SPLIT:x86-64 = "1"
