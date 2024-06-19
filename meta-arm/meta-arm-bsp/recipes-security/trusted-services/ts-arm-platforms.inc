FILESEXTRAPATHS:prepend:corstone1000 := "${THISDIR}/corstone1000:"

COMPATIBLE_MACHINE:corstone1000 = "corstone1000"
SRC_URI:append:corstone1000  = " \
    file://0001-Add-stub-capsule-update-service-components.patch \
    file://0002-Fixes-in-AEAD-for-psa-arch-test-54-and-58.patch \
    file://0003-FMP-Support-in-Corstone1000.patch \
    file://0004-smm_gateway-GetNextVariableName-Fix.patch     \
    file://0005-plat-corstone1000-add-compile-definitions-for-ECP_DP.patch \
    file://0006-plat-corstone1000-Use-the-stateless-platform-service.patch \
    file://0007-plat-corstone1000-Initialize-capsule-update-provider.patch \
    file://0008-plat-corstone1000-add-client_id-for-FMP-service.patch \
    file://0009-Remove-Werror-flag.patch \
    file://0010-Remove-PLATFORM_HAS_ATTEST_PK-define-from-IAT-test.patch \
    file://0011-Fix-Avoid-redefinition-of-variables.patch \
    file://0012-Fix-GetNextVariableName-NameSize-input.patch \
    file://0013-Fix-error-handling-of-variable-index-loading.patch \
    file://0014-Provide-crypto-api-to-create-uefi-priv-var-fingerpri.patch \
    file://0015-Add-timestamp-validation-for-uefi-variables.patch \
    file://0016-Isolate-common-uefi-variable-authentication-steps.patch \
    file://0017-Implement-Private-Authenticated-Variable-verificatio.patch \
    file://0018-Change-RSS_COMMS-cmake-variables-to-cahce-vars.patch \
    "

COMPATIBLE_MACHINE:fvp-base = "fvp-base"
TS_PLATFORM:fvp-base = "arm/fvp/fvp_base_revc-2xaemv8a"
