require fvp-envelope.inc

LICENSE:append = " & Artistic-2.0 & BSL-1.0 & BSD-2-Clause & Unlicense"

SUMMARY = "Arm Fixed Virtual Platform - Armv-A Base RevC Architecture Envelope Model FVP"
LIC_FILES_CHKSUM = "file://license_terms/license_agreement.txt;md5=1a33828e132ba71861c11688dbb0bd16 \
                    file://license_terms/third_party_licenses/third_party_licenses.txt;md5=c51b57b6a4731881849eb0e2e2b6d799 \
                    file://license_terms/third_party_licenses/arm_license_management_utilities/third_party_licenses.txt;md5=abcaafefc7b7a0cdf6664c51f9075c5b"

SRC_URI = "https://developer.arm.com/-/cdn-downloads/permalink/FVPs-Architecture/${PV_URL_SHORT}/${MODEL_CODE}_${PV_URL}_${FVP_ARCH}.tgz;subdir=${BP};name=fvp-${HOST_ARCH}"
SRC_URI[fvp-aarch64.sha256sum] = "a380e271f14ce7cf99158018368ac86fe8c305f82c7d332a93eb08bf7a172571"
SRC_URI[fvp-x86_64.sha256sum] = "0f6d67d834a8ed5dff3c863e0e3545ef39c736405ea8227577ac004cffd66e93"

# The CSS used in the FVP homepage make it too difficult to query with the tooling currently in Yocto
UPSTREAM_VERSION_UNKNOWN = "1"

MODEL_CODE = "FVP_Base_RevC-2xAEMvA"

COMPATIBLE_HOST = "(aarch64|x86_64).*-linux"

require remove-execstack.inc
REMOVE_EXECSTACKS:x86-64 = "${FVPDIR}/models/${FVP_ARCH_DIR}*/libarmctmodel.so"
