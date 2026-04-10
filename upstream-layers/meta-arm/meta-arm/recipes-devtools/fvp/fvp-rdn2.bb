require fvp-ecosystem.inc

MODEL = "RD-N2"
MODEL_CODE = "FVP_RD_N2"
PV = "11.25.23"

SRC_URI = "https://developer.arm.com/-/cdn-downloads/permalink/FVPs-Neoverse-Infrastructure/${MODEL}/${MODEL_CODE}_${PV_URL}_${FVP_ARCH}.tgz;subdir=${BP};name=fvp-${HOST_ARCH}"
SRC_URI[fvp-aarch64.sha256sum] = "ae3a3e85ae307dffcc1221d0f30efbb6bc22b61fd508305b984f294407659bdd"
SRC_URI[fvp-x86_64.sha256sum] = "89c6d5a784d0b76168fb187e366de35f7de5aa7583a148b206b2aee2bc486da2"

# The CSS used in the FVP homepage make it too difficult to query with the tooling currently in Yocto
UPSTREAM_VERSION_UNKNOWN = "1"

LIC_FILES_CHKSUM = "file://license_terms/license_agreement.txt;md5=1a33828e132ba71861c11688dbb0bd16 \
                    file://license_terms/third_party_licenses/third_party_licenses.txt;md5=b9005e55057311e41efe02ccfea8ea72"

COMPATIBLE_HOST = "(aarch64|x86_64).*-linux"

require remove-execstack.inc
REMOVE_EXECSTACKS:x86-64 = "${FVPDIR}/models/${FVP_ARCH_DIR}*/libarmctmodel.so"
