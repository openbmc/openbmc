require fvp-ecosystem.inc

MODEL = "Corstone-1000-23"
MODEL_CODE = "FVP_Corstone_1000"
PV = "11.17_23"

SRC_URI = "https://developer.arm.com/-/media/Arm%20Developer%20Community/Downloads/OSS/FVP/${MODEL}/Linux/${MODEL_CODE}_${PV}.tgz;subdir=${BP}"
SRC_URI[sha256sum] = "00ccb72d02c90e2424d24a625d275cabf8ea8dc024713985208f618bb88d1934"

LIC_FILES_CHKSUM = "file://license_terms/license_agreement.txt;md5=1a33828e132ba71861c11688dbb0bd16 \
                    file://license_terms/third_party_licenses.txt;md5=41029e71051b1c786bae3112a29905a7"
