require fvp-envelope.inc

SUMMARY = "Arm Fixed Virtual Platform - Armv-A Base RevC Architecture Envelope Model FVP"
LIC_FILES_CHKSUM = "file://license_terms/license_agreement.txt;md5=1a33828e132ba71861c11688dbb0bd16 \
                    file://license_terms/third_party_licenses/third_party_licenses.txt;md5=34a1ba318d745f05e6197def68ea5411 \
                    file://license_terms/third_party_licenses/arm_license_management_utilities/third_party_licenses.txt;md5=2e53bda6ff2db4c35d69944b93926c9f"

SRC_URI[fvp-aarch64.sha256sum] = "e14e6cbd5c3ec8e1e2e79965e994c9235e136480e102f5c43de31162263b6361"
SRC_URI[fvp-x86_64.sha256sum] = "f9db2076e3a63e2f8eb2ea9ed60f30db04e5f81f535bc7e3ed24e270d857ea6f"

MODEL_CODE = "FVP_Base_RevC-2xAEMvA"

COMPATIBLE_HOST = "(aarch64|x86_64).*-linux"
