FILESEXTRAPATHS:prepend:corstone1000 := "${THISDIR}/corstone1000:"

COMPATIBLE_MACHINE:corstone1000 = "corstone1000"
SRC_URI:append:corstone1000  = " \
    file://0001-Add-stub-capsule-update-service-components.patch;patchdir=../trusted-services \
    file://0002-Fixes-in-AEAD-for-psa-arch-test-54-and-58.patch;patchdir=../trusted-services \
    file://0003-FMP-Support-in-Corstone1000.patch;patchdir=../trusted-services \
    file://0004-GetNextVariableName-Fix.patch;patchdir=../trusted-services     \
    file://0005-plat-corstone1000-add-compile-definitions-for-ECP_DP.patch;patchdir=../trusted-services \
    file://0006-plat-corstone1000-Use-the-stateless-platform-service.patch;patchdir=../trusted-services \
    file://0007-plat-corstone1000-Initialize-capsule-update-provider.patch;patchdir=../trusted-services \
    "


COMPATIBLE_MACHINE:n1sdp = "n1sdp"
