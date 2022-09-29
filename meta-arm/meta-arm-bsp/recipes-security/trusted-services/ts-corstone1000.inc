FILESEXTRAPATHS:prepend := "${THISDIR}/secure-partitions/corstone1000:"

COMPATIBLE_MACHINE = "corstone1000"

LIC_FILES_CHKSUM += "file://../mbedtls/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
                     file://../nanopb/LICENSE.txt;md5=9db4b73a55a3994384112efcdb37c01f \
                     file://../openamp/LICENSE.md;md5=a8d8cf662ef6bf9936a1e1413585ecbf \
		     file://../libmetal/LICENSE.md;md5=fe0b8a4beea8f0813b606d15a3df3d3c \
		     file://../psa-arch-tests/LICENSE.md;md5=2a944942e1496af1886903d274dedb13 \
		     "

SRC_URI:append = " \
                  ${SRC_URI_MBEDTLS} ${SRC_URI_NANOPB} ${SRC_URI_OPENAMP} ${SRC_URI_LIBMETAL} ${SRC_URI_ARCH-TESTS}\
                  file://0001-tools-cmake-common-applying-lowercase-project-conven.patch \
                  file://0002-fix-EARLY_TA_PATHS-env-variable.patch \
                  file://0003-se-proxy-dts-add-se-proxy-as-child-node.patch \
                  file://0004-Update-mm-comm-buffer-region-in-dts-file.patch \
                  file://0005-Configure-NV-storage-macro.patch \
                  file://0006-Use-device-region.patch \
                  file://0007-Add-openamp-to-SE-proxy-deployment.patch \
                  file://0008-Implement-mhu-driver-and-the-OpenAmp-conversion-laye.patch \
                  file://0009-Add-openamp-rpc-caller.patch \
                  file://0010-add-psa-client-definitions-for-ff-m.patch \
                  file://0011-Add-common-service-component-to-ipc-support.patch \
                  file://0012-Add-secure-storage-ipc-backend.patch \
                  file://0013-Use-secure-storage-ipc-and-openamp-for-se_proxy.patch \
                  file://0014-Add-uefi-variable-append-write-support.patch \
                  file://0015-Add-UEFI-variable-support-for-QueryVariableInfo.patch \
                  file://0016-Add-uefi-test-deployment.patch \
                  file://0017-Fix-interface-ID-parameter-setting-in-sp-ffarpc_call.patch \
                  file://0018-Support-FFARPC-call-requests-with-no-shared-buffer.patch \
                  file://0019-Run-psa-arch-test.patch \
                  file://0020-Use-address-instead-of-pointers.patch \
                  file://0021-Add-psa-ipc-attestation-to-se-proxy.patch \
                  file://0022-Setup-its-backend-as-openamp-rpc-using-secure-storag.patch \
                  file://0023-add-psa-ipc-crypto-backend.patch \
                  file://0024-Increase-SMM-gateway-UEFI-variable-macro-value.patch \
                  file://0025-Add-stub-capsule-update-service-components.patch \
                  file://0026-Add-logs-to-functions-in-SMM-gateway-SP.patch \
                  file://0027-Configure-storage-size.patch \
                  file://0028-Revert-Add-uefi-variable-append-write-support.patch \
                  file://0029-Change-UID-of-variable-index-in-SMM.patch \
                  file://0030-Add-missing-features-to-setVariable.patch \
                  file://0031-Add-invalid-parameter-check-in-getNextVariableName.patch \
                  file://0032-smm_gateway-add-checks-for-null-attributes.patch \
		  file://0033-Enhance-mbedtls-fetch-process.patch \
		  file://0034-Fix-format-specifier-in-logging_caller.patch \
		  file://0035-Update-refspecs-for-mbedtls-and-psa-arch-tests-for-v.patch \
		  file://0036-Separate-sign-verify-message-and-hash-operations.patch \
		  file://0037-Add-defence-against-uninitialised-multi-part-transac.patch \
		  file://0038-Integrate-AEAD-operation-support.patch \
		  file://0039-Add-IV-generation-to-one-shot-cipher-operation.patch \
		  file://0040-Fix-multi-part-termination-on-error.patch \
		  file://0041-Abort-AEAD-operation-if-client-provided-buffer-is-to.patch \
		  file://0042-Peg-to-updated-t_cose-version-fc3a4b2c.patch \
		  file://0043-pass-sysroot_yocto.patch \
		  file://0044-Fix-Crypto-interface-structure-aligned-with-tf-m-cha.patch \
		  file://0045-Integrate-remaining-psa-ipc-client-APIs.patch \
		  file://0046-Fix-update-psa_set_key_usage_flags-definition-to-the.patch \
		  file://0047-Fixes-in-AEAD-for-psa-arch-test-54-and-58.patch \
		  file://0003-corstone1000-port-crypto-config.patch;patchdir=../psa-arch-tests \
                  file://0048-Fix-UEFI-get_variable-with-small-buffer.patch \
                  "

SRC_URI_MBEDTLS = "git://github.com/ARMmbed/mbedtls.git;protocol=https;branch=development;name=mbedtls;destsuffix=git/mbedtls"
SRCREV_mbedtls = "d65aeb37349ad1a50e0f6c9b694d4b5290d60e49"

SRC_URI_NANOPB = "git://github.com/nanopb/nanopb.git;name=nanopb;protocol=https;branch=master;destsuffix=git/nanopb"
SRCREV_nanopb = "df0e92f474f9cca704fe2b31483f0b4d1b1715a4"

SRC_URI_OPENAMP = "git://github.com/OpenAMP/open-amp.git;name=openamp;protocol=https;branch=main;destsuffix=git/openamp"
SRCREV_openamp = "347397decaa43372fc4d00f965640ebde042966d"

SRC_URI_LIBMETAL = "git://github.com/OpenAMP/libmetal.git;name=libmetal;protocol=https;branch=main;destsuffix=git/libmetal"
SRCREV_libmetal = "f252f0e007fbfb8b3a52b1d5901250ddac96baad"

SRC_URI_ARCH-TESTS = "git://github.com/bensze01/psa-arch-tests.git;name=psa-arch-tests;protocol=https;nobranch=1;destsuffix=git/psa-arch-tests"
SRCREV_psa-arch-tests = "5d1a87f9c0a82e1632a3145687b4c8d7cbbeed2d"

TS_ENVIRONMENT_LINUX = "arm-linux"
TS_PLATFORM = "arm/corstone1000"
TS_ENVIRONMENT = "opteesp"
SP_PACKAGING_METHOD = "embedded"

# Secure Enclave proxy secure partition
TS_DEPLOYMENTS += "'deployments/se-proxy/${TS_ENVIRONMENT}'"

# smm-gateway secure partition
TS_DEPLOYMENTS += "'deployments/smm-gateway/${TS_ENVIRONMENT}'"

PSA_API_TESTS = "deployments/psa-api-test/protected_storage/${TS_ENVIRONMENT_LINUX}"
PSA_API_TESTS += "deployments/psa-api-test/internal_trusted_storage/${TS_ENVIRONMENT_LINUX}"
PSA_API_TESTS += "deployments/psa-api-test/initial_attestation/${TS_ENVIRONMENT_LINUX}"
PSA_API_TESTS += "deployments/psa-api-test/crypto/${TS_ENVIRONMENT_LINUX}"

# ffa-debugfs-mod provides arm_ffa_user.h needed by psa-arch-tests source-code
DEPENDS += "ffa-debugfs-mod"

do_configure:append() {
    for PSA_API_TEST in ${PSA_API_TESTS}; do
        cmake \
	    -DSYSROOT_YOCTO=${RECIPE_SYSROOT} \
	    -S ${S}/$PSA_API_TEST -B "${B}/$PSA_API_TEST"
    done
}

do_compile:append() {
    for PSA_API_TEST in ${PSA_API_TESTS}; do
        cmake --build "${B}/$PSA_API_TEST"
    done
}

do_install:append() {
    for PSA_API_TEST in ${PSA_API_TESTS}; do
        install -d -m 0755 ${D}${libdir}/${PSA_API_TEST}
        install -d -m 0755 ${D}${includedir}/${PSA_API_TEST}
        psafile_fullpath=`ls ${B}/${PSA_API_TEST}/psa-*`
        psafile_filename="`basename -s .bin ${psafile_fullpath}`"
        install -D -p -m 0755 ${psafile_fullpath} ${D}/${bindir}/${psafile_filename}
   done
   cp -rf ${B}/${PSA_API_TEST}/libts_install/arm-linux/lib/*.so* ${D}/${libdir}
}

FILES:${PN}-dev = "${includedir}/deployments/psa-api-test/"
INSANE_SKIP:${PN}-psa-api-tests += "rpaths dev-so buildpaths"
PACKAGES += "${PN}-psa-api-tests"
FILES:${PN}-psa-api-tests = "${libdir} ${bindir}"
