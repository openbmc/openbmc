require fvp-ecosystem.inc

MODEL = "TC1"
MODEL_CODE = "FVP_TC1"
PV = "11.18.28_Linux64"

SRC_URI = "https://developer.arm.com/-/media/Arm%20Developer%20Community/Downloads/OSS/FVP/TotalCompute/Total%20Compute%20Update%202022/${MODEL_CODE}_${PV_URL}.tgz;subdir=${BP}"
SRC_URI[sha256sum] = "3a2b32ecf34dc9581482d6fc682a9378ba6ed151ea9b68914b4ebad39fb5cacf"

LIC_FILES_CHKSUM = "file://license_terms/license_agreement.txt;md5=a50d186fffa51ed55599183aad911298 \
                    file://license_terms/third_party_licenses/third_party_licenses.txt;md5=34a1ba318d745f05e6197def68ea5411"
