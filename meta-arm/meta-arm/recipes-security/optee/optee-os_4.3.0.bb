require recipes-security/optee/optee-os.inc

DEPENDS += "dtc-native"

FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRCREV = "1c0d52ace3c237ca6276cafb5c73f699a75c1d40"
SRC_URI += " \
    file://0003-optee-enable-clang-support.patch \
    file://0001-mk-compile.mk-remove-absolute-build-time-paths.patch \
    file://0001-compile.mk-use-CFLAGS-from-environment.patch \
    file://0002-link.mk-use-CFLAGS-with-version.o.patch \
    file://0003-link.mk-generate-version.o-in-link-out-dir.patch \
"
