FILESEXTRAPATHS:prepend := "${THISDIR}/corstone1000:"

SRC_URI:append = " \
    file://0001-Add-openamp-to-SE-proxy-deployment.patch \
    file://0002-Implement-mhu-driver-and-the-OpenAmp-conversion-laye.patch \
    file://0003-Add-openamp-rpc-caller.patch \
    file://0004-add-psa-client-definitions-for-ff-m.patch \
    file://0005-Add-common-service-component-to-ipc-support.patch \
    file://0006-Add-secure-storage-ipc-backend.patch \
    file://0007-Use-secure-storage-ipc-and-openamp-for-se_proxy.patch \
    file://0008-Run-psa-arch-test.patch \
    file://0009-Use-address-instead-of-pointers.patch \
    file://0010-Add-psa-ipc-attestation-to-se-proxy.patch \
    file://0011-Setup-its-backend-as-openamp-rpc-using-secure-storag.patch \
    file://0012-add-psa-ipc-crypto-backend.patch \
    file://0013-Add-stub-capsule-update-service-components.patch \
    file://0014-Configure-storage-size.patch \
    file://0015-Fix-Crypto-interface-structure-aligned-with-tf-m-cha.patch \
    file://0016-Integrate-remaining-psa-ipc-client-APIs.patch \
    file://0017-Fix-update-psa_set_key_usage_flags-definition-to-the.patch \
    file://0018-Fixes-in-AEAD-for-psa-arch-test-54-and-58.patch \
    file://0019-plat-corstone1000-change-default-smm-values.patch \
    file://0020-FMP-Support-in-Corstone1000.patch \
    "


EXTRA_OECMAKE:append = "-DMM_COMM_BUFFER_ADDRESS="0x00000000 0x02000000" \
    -DMM_COMM_BUFFER_PAGE_COUNT="1" \
    "
