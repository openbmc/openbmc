FILESEXTRAPATHS:prepend := "${THISDIR}/corstone1000/psa-apitest:"

SRC_URI:append  = " \
    file://0001-corstone1000-port-crypto-config.patch;patchdir=../ts/ts-external/psatest \
    file://0002-corstone1000-Disable-obsolete-algorithms.patch;patchdir=../ts/ts-external/psatest \
    file://0003-corstone1000-Disable-SHA512-384.patch;patchdir=../ts/ts-external/psatest \
    file://0004-Remove-RSA-support.patch;patchdir=../ts/ts-external/psatest \
"
