FILESEXTRAPATHS:prepend := "${THISDIR}/corstone1000:"
FILESEXTRAPATHS:prepend := "${THISDIR}/corstone1000/${PN}:"

SRC_URI:append:corstone1000 = " \
            file://0001-corstone1000-port-crypto-config.patch;patchdir=../psatest \
            file://0018-Fixes-in-AEAD-for-psa-arch-test-54-and-58.patch;patchdir=../trusted-services \
           "
