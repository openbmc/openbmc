SUMMARY = "Meta package for building a installable Go toolchain"
LICENSE = "MIT"

inherit populate_sdk

TOOLCHAIN_HOST_TASK:append = " \
    packagegroup-go-cross-canadian-${MACHINE} \
"

TOOLCHAIN_TARGET_TASK:append = " \
    ${@multilib_pkg_extend(d, 'packagegroup-go-sdk-target')} \
"
