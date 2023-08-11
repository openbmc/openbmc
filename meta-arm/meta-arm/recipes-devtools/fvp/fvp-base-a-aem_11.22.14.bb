require fvp-envelope.inc

SUMMARY = "Arm Fixed Virtual Platform - Armv-A Base RevC Architecture Envelope Model FVP"
LIC_FILES_CHKSUM = "file://license_terms/license_agreement.txt;md5=1a33828e132ba71861c11688dbb0bd16 \
                    file://license_terms/third_party_licenses/third_party_licenses.txt;md5=34a1ba318d745f05e6197def68ea5411 \
                    file://license_terms/third_party_licenses/arm_license_management_utilities/third_party_licenses.txt;md5=c09526c02e631abb95ad61528892552d"

SRC_URI[fvp-aarch64.sha256sum] = "6964dbe0e297a5a6b5abd290d09e883923b5150e087f285fcfb80077525bfe6e"
SRC_URI[fvp-x86_64.sha256sum] = "eb0f5ca855fb8b0321e137b82306ac8a6b534a5625366ff10e20b3f68df533a4"

MODEL_CODE = "FVP_Base_RevC-2xAEMvA"

COMPATIBLE_HOST = "(aarch64|x86_64).*-linux"
