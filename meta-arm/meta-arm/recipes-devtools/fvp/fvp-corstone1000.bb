require fvp-ecosystem.inc

MODEL = "Corstone-1000-23"
MODEL_CODE = "FVP_Corstone_1000"
PV = "11.19_21"

SRC_URI = "https://developer.arm.com/-/media/Arm%20Developer%20Community/Downloads/OSS/FVP/${MODEL}/Linux/${MODEL_CODE}_${PV}_${FVP_ARCH}.tgz;subdir=${BP}"
SRC_URI[sha256sum] = "dbdcb8b0c206fd56fd2296fe338a62902eb978883ba07f4da28440e180383b24"

LIC_FILES_CHKSUM = "file://license_terms/license_agreement.txt;md5=1a33828e132ba71861c11688dbb0bd16 \
                    file://license_terms/third_party_licenses/third_party_licenses.txt;md5=34a1ba318d745f05e6197def68ea5411"
