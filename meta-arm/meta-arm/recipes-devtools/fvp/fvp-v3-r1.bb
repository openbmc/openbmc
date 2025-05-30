require fvp-ecosystem.inc

MODEL = "RD-V3-r1"
MODEL_CODE = "FVP_RD_V3_R1"
PV = "11.27.51"

SRC_URI = "https://developer.arm.com/-/cdn-downloads/permalink/FVPs-Neoverse-Infrastructure/${MODEL}/${MODEL_CODE}_${PV_URL}_${FVP_ARCH}.tgz;subdir=${BP};name=fvp-${HOST_ARCH}"
SRC_URI[fvp-aarch64.sha256sum] = "6b25961a7dcac99ff5d2c63f297e1598b05e67938d71353b37c82e3d58d7ac3c"
SRC_URI[fvp-x86_64.sha256sum] = "72f659ee3554ab0330984f35b2d640ea6553bbcbbf77cc485555be14277e4bc3"

# The CSS used in the FVP homepage make it too difficult to query with the tooling currently in Yocto
UPSTREAM_VERSION_UNKNOWN = "1"

LIC_FILES_CHKSUM = "file://license_terms/license_agreement.txt;md5=1a33828e132ba71861c11688dbb0bd16 \
                    file://license_terms/third_party_licenses/third_party_licenses.txt;md5=9ddd501715f7e1fed82c57b260b020ba"

COMPATIBLE_HOST = "(aarch64|x86_64).*-linux"
