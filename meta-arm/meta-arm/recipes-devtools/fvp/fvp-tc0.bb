require fvp-ecosystem.inc

MODEL = "TotalCompute"
MODEL_CODE = "FVP_TC0"
PV = "11.17.18"

# Unconventional URI structure for this release
SRC_URI = "https://developer.arm.com/-/media/Arm%20Developer%20Community/Downloads/OSS/FVP/TotalCompute/Total%20Compute%20Update%202022/FVP_TC0_11.17_18.tgz;subdir=${BP}"
SRC_URI[sha256sum] = "0bd78354e036a7e92bd7f8cbd78cd2b5197dc0872fe2b25c95ea734929fe83b8"

LIC_FILES_CHKSUM = "file://license_terms/license_agreement.txt;md5=1a33828e132ba71861c11688dbb0bd16 \
                    file://license_terms/third_party_licenses.txt;md5=41029e71051b1c786bae3112a29905a7"
