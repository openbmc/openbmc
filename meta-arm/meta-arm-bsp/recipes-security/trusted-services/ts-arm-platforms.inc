FILESEXTRAPATHS:prepend:corstone1000 := "${THISDIR}/corstone1000:"

COMPATIBLE_MACHINE:corstone1000 = "corstone1000"
SRC_URI:append:corstone1000  = " \
    file://0001-Add-stub-capsule-update-service-components.patch;patchdir=../trusted-services \
    file://0002-Fixes-in-AEAD-for-psa-arch-test-54-and-58.patch;patchdir=../trusted-services \
    file://0003-FMP-Support-in-Corstone1000.patch;patchdir=../trusted-services \
    "


COMPATIBLE_MACHINE:n1sdp = "n1sdp"
