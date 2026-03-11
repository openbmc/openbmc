FILESEXTRAPATHS:prepend:corstone1000 := "${THISDIR}/corstone1000/psa-apitest:"

include ts-arm-platforms.inc

SRC_URI:append:corstone1000  = " \
    file://0001-corstone1000-port-crypto-config.patch;patchdir=../psatest \
    file://0002-corstone1000-Disable-obsolete-algorithms.patch;patchdir=../psatest \
    file://0003-corstone1000-Disable-SHA512-384.patch;patchdir=../psatest \
    "
