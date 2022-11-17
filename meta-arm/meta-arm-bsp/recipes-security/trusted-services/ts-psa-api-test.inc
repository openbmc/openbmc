FILESEXTRAPATHS:prepend := "${THISDIR}/corstone1000/psa-apitest:"

include ts-corstone1000.inc

SRC_URI:append:corstone1000  = " \
    file://0001-corstone1000-port-crypto-config.patch;patchdir=../psatest \
    "
