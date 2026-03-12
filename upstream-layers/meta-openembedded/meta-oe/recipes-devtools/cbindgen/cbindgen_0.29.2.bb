SUMMARY = "cbindgen creates C/C++11 headers for Rust libraries which expose a public C API"
HOMEPAGE = "https://github.com/mozilla/cbindgen"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9741c346eef56131163e13b9db1241b3"

require cbindgen-crates.inc
SRC_URI += "git://github.com/mozilla/cbindgen.git;protocol=https;branch=main;tag=v${PV}"
SRC_URI += "file://0001-Use-libc-SYS_futex_time64-on-riscv32.patch;patchdir=${UNPACKDIR}/cargo_home/bitbake/getrandom-0.3.3/"

SRCREV = "76f41c090c0587d940a0ef81a41c8b995f074926"

inherit cargo pkgconfig

BBCLASSEXTEND += "native"
