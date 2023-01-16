FILESEXTRAPATHS:prepend:corstone1000 := "${THISDIR}/corstone1000:"

COMPATIBLE_MACHINE:corstone1000 = "corstone1000"
SRC_URI:append:corstone1000  = " \
    file://0001-Add-openamp-to-SE-proxy-deployment.patch;patchdir=../trusted-services \
    file://0002-Implement-mhu-driver-and-the-OpenAmp-conversion-laye.patch;patchdir=../trusted-services \
    file://0003-Add-openamp-rpc-caller.patch;patchdir=../trusted-services \
    file://0004-add-psa-client-definitions-for-ff-m.patch;patchdir=../trusted-services \
    file://0005-Add-common-service-component-to-ipc-support.patch;patchdir=../trusted-services \
    file://0006-Add-secure-storage-ipc-backend.patch;patchdir=../trusted-services \
    file://0007-Use-secure-storage-ipc-and-openamp-for-se_proxy.patch;patchdir=../trusted-services \
    file://0008-Run-psa-arch-test.patch;patchdir=../trusted-services \
    file://0009-Use-address-instead-of-pointers.patch;patchdir=../trusted-services \
    file://0010-Add-psa-ipc-attestation-to-se-proxy.patch;patchdir=../trusted-services \
    file://0011-Setup-its-backend-as-openamp-rpc-using-secure-storag.patch;patchdir=../trusted-services;patchdir=../trusted-services \
    file://0012-add-psa-ipc-crypto-backend.patch;patchdir=../trusted-services \
    file://0013-Add-stub-capsule-update-service-components.patch;patchdir=../trusted-services \
    file://0014-Configure-storage-size.patch;patchdir=../trusted-services \
    file://0015-Fix-Crypto-interface-structure-aligned-with-tf-m-cha.patch;patchdir=../trusted-services;patchdir=../trusted-services \
    file://0016-Integrate-remaining-psa-ipc-client-APIs.patch;patchdir=../trusted-services \
    file://0017-Fix-update-psa_set_key_usage_flags-definition-to-the.patch;patchdir=../trusted-services;patchdir=../trusted-services \
    file://0018-Fixes-in-AEAD-for-psa-arch-test-54-and-58.patch;patchdir=../trusted-services \
    file://0019-plat-corstone1000-change-default-smm-values.patch;patchdir=../trusted-services \
    file://0020-FMP-Support-in-Corstone1000.patch;patchdir=../trusted-services \
    file://0021-smm_gateway-add-checks-for-null-attributes.patch;patchdir=../trusted-services \
    file://0022-GetNextVariableName-Fix.patch;patchdir=../trusted-services \
    file://0023-Use-the-stateless-platform-service.patch;patchdir=../trusted-services \
    "

COMPATIBLE_MACHINE:n1sdp = "n1sdp"
