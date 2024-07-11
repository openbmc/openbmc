require fvp-envelope.inc

SUMMARY = "Arm Fixed Virtual Platform - Armv-A Base RevC Architecture Envelope Model FVP"
LIC_FILES_CHKSUM = "file://license_terms/license_agreement.txt;md5=1a33828e132ba71861c11688dbb0bd16 \
                    file://license_terms/third_party_licenses/third_party_licenses.txt;md5=b9005e55057311e41efe02ccfea8ea72 \
                    file://license_terms/third_party_licenses/arm_license_management_utilities/third_party_licenses.txt;md5=c09526c02e631abb95ad61528892552d"

SRC_URI[fvp-aarch64.sha256sum] = "22096fc2267ad776abe0ff32d0d3b870c9fae10036d9c16f4f0fe4a64487a11e"
SRC_URI[fvp-x86_64.sha256sum] = "5f33707a1bdaa96a933b89949f28643110ad80ac9835a75f139c200b64a394dc"

# The CSS used in the FVP homepage make it too difficult to query with the tooling currently in Yocto
UPSTREAM_VERSION_UNKNOWN = "1"

MODEL_CODE = "FVP_Base_RevC-2xAEMvA"

COMPATIBLE_HOST = "(aarch64|x86_64).*-linux"
