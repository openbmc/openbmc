require fvp-envelope.inc

SUMMARY = "Arm Fixed Virtual Platform - Armv-A Base RevC Architecture Envelope Model FVP"
LIC_FILES_CHKSUM = "file://license_terms/license_agreement.txt;md5=1a33828e132ba71861c11688dbb0bd16 \
                    file://license_terms/third_party_licenses/third_party_licenses.txt;md5=b9005e55057311e41efe02ccfea8ea72 \
                    file://license_terms/third_party_licenses/arm_license_management_utilities/third_party_licenses.txt;md5=c09526c02e631abb95ad61528892552d"

SRC_URI[fvp-aarch64.sha256sum] = "7a3593dafd3af6897b3a0a68f66701201f8f3e02a3d981ba47494b2f18853648"
SRC_URI[fvp-x86_64.sha256sum] = "0f132334834cbc66889a62dd72057c976d7c7dfcfeec21799e9c78fb2ce24720"

MODEL_CODE = "FVP_Base_RevC-2xAEMvA"

COMPATIBLE_HOST = "(aarch64|x86_64).*-linux"
