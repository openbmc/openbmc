require fvp-ecosystem.inc

MODEL = "RD-V3-r1"
MODEL_CODE = "FVP_RD_V3_R1"
PV = "11.29.35"

SRC_URI = "https://developer.arm.com/-/cdn-downloads/permalink/FVPs-Neoverse-Infrastructure/${MODEL}/${MODEL_CODE}_${PV_URL}_${FVP_ARCH}.tgz;subdir=${BP};name=fvp-${HOST_ARCH}"
SRC_URI[fvp-aarch64.sha256sum] = "feb805bcb76921c926ff582c35f5f8cedfb9f15294cf873243de1018bf32e30f"
SRC_URI[fvp-x86_64.sha256sum] = "f1c77d0c50dea01a5dd4ea6fbe548919abc0d63f281f8378f6d9fa83932e36cc"

# The CSS used in the FVP homepage make it too difficult to query with the tooling currently in Yocto
UPSTREAM_VERSION_UNKNOWN = "1"

LIC_FILES_CHKSUM = "file://license_terms/license_agreement.txt;md5=1a33828e132ba71861c11688dbb0bd16 \
                    file://license_terms/third_party_licenses/third_party_licenses.txt;md5=41f1533ebfbd4f1d32cfc82b55f6729a"

COMPATIBLE_HOST = "(aarch64|x86_64).*-linux"
