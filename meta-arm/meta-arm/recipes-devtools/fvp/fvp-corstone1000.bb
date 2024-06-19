require fvp-ecosystem.inc

MODEL = "Corstone-1000"
MODEL_CODE = "FVP_Corstone_1000"
PV = "11.23.25"

SRC_URI = "https://developer.arm.com/-/media/Arm%20Developer%20Community/Downloads/OSS/FVP/${MODEL}/${MODEL_CODE}_${PV_URL}_${FVP_ARCH}.tgz;subdir=${BP};name=fvp-${HOST_ARCH}"
SRC_URI[fvp-aarch64.sha256sum] = "e299e81d5fa8b3d2afee0850fd03be31c1a1c3fad07f79849c63e46ee5e36acc"
SRC_URI[fvp-x86_64.sha256sum] = "ec34c9564ccb5b1eb62fc2757673343a353db1d116a7cb1b5f82f9d985d99cdf"

UPSTREAM_CHECK_REGEX = "${MODEL_CODE}_(?P<pver>(\d+[\.\-_]*)+)_${FVP_ARCH}\.tgz"

LIC_FILES_CHKSUM = "file://license_terms/license_agreement.txt;md5=1a33828e132ba71861c11688dbb0bd16 \
                    file://license_terms/third_party_licenses/third_party_licenses.txt;md5=0c32ac6f58ebff83065105042ab98211"

COMPATIBLE_HOST = "(aarch64|x86_64).*-linux"
