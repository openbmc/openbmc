require fvp-envelope.inc

LICENSE:append = " & Artistic-2.0 & BSL-1.0 & BSD-2-Clause & Unlicense"

SUMMARY = "Arm Fixed Virtual Platform - Armv-A Base RevC Architecture Envelope Model FVP"
LIC_FILES_CHKSUM = "file://license_terms/license_agreement.txt;md5=1a33828e132ba71861c11688dbb0bd16 \
                    file://license_terms/third_party_licenses/third_party_licenses.txt;md5=92ea58bb219213dfdc48111b693019b6 \
                    file://license_terms/third_party_licenses/arm_license_management_utilities/third_party_licenses.txt;md5=abcaafefc7b7a0cdf6664c51f9075c5b"

SRC_URI[fvp-aarch64.sha256sum] = "8fce0dc6346c37b0df4809d08b599463c880b6375f9b58d89a73fa6f9ab2e129"
SRC_URI[fvp-x86_64.sha256sum] = "8a8c4a609c32890ce96799aa7319fff3ecf752c7a9dab52b2e968cb80cc1836f"

# The CSS used in the FVP homepage make it too difficult to query with the tooling currently in Yocto
UPSTREAM_VERSION_UNKNOWN = "1"

MODEL_CODE = "FVP_Base_RevC-2xAEMvA"

COMPATIBLE_HOST = "(aarch64|x86_64).*-linux"
