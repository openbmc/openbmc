require fvp-envelope.inc

LICENSE:append = " & Artistic-2.0 & BSL-1.0 & BSD-2-Clause & Unlicense"

SUMMARY = "Arm Fixed Virtual Platform - Armv-A Base RevC Architecture Envelope Model FVP"
LIC_FILES_CHKSUM = "file://license_terms/license_agreement.txt;md5=1a33828e132ba71861c11688dbb0bd16 \
                    file://license_terms/third_party_licenses/third_party_licenses.txt;md5=5e3a194978d88188b5a0eebd263f2ca8 \
                    file://license_terms/third_party_licenses/arm_license_management_utilities/third_party_licenses.txt;md5=4f61d2f0d834d5c29d53d828572fb6fc"

SRC_URI[fvp-aarch64.sha256sum] = "38cfb5244fe898bed5c3ba46ec6e3654638e29085ef597b9ed92f36be08adf1d"
SRC_URI[fvp-x86_64.sha256sum] = "527920d1873472de045c844fa6f5b8679511ad4f9996058eb9e075b0f0eada10"

# The CSS used in the FVP homepage make it too difficult to query with the tooling currently in Yocto
UPSTREAM_VERSION_UNKNOWN = "1"

MODEL_CODE = "FVP_Base_RevC_AEMvA"

COMPATIBLE_HOST = "(aarch64|x86_64).*-linux"

INSANE_SKIP:${PN} += "dev-so"
