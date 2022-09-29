require fvp-ecosystem.inc

MODEL = "Neoverse-N1"
MODEL_CODE = "FVP_RD_N1_edge"
PV = "11.17.29"

SRC_URI = "https://developer.arm.com/-/media/Arm%20Developer%20Community/Downloads/OSS/FVP/${MODEL}/${MODEL_CODE}_${PV_URL}_Linux64.tgz;subdir=${BP}"
SRC_URI[sha256sum] = "76f5d6ec50b64fad6d8d901101d9ae2c62805f50fcfd0edb125bc2c68de8c8f2"

LIC_FILES_CHKSUM = "file://license_terms/license_agreement.txt;md5=1a33828e132ba71861c11688dbb0bd16 \
                    file://license_terms/third_party_licenses.txt;md5=41029e71051b1c786bae3112a29905a7"
