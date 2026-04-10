require fvp-ecosystem.inc

MODEL = "RD-V2"
MODEL_CODE = "FVP_RD_V2"
PV = "11.24.12"

SRC_URI = "https://developer.arm.com/-/cdn-downloads/permalink/FVPs-Neoverse-Infrastructure/${MODEL}/${MODEL_CODE}_${PV_URL}_${FVP_ARCH}.tgz;subdir=${BP};name=fvp-${HOST_ARCH}"
SRC_URI[fvp-aarch64.sha256sum] = "fa43cc506e3799afb73c646f93c2ea509dc1081c0e993c4a7bf83b082b896e5f"
SRC_URI[fvp-x86_64.sha256sum] = "74199d4766e96ed86b071bf343a0a60d876accef83be41be3dbb9557b4455635"

# The CSS used in the FVP homepage make it too difficult to query with the tooling currently in Yocto
UPSTREAM_VERSION_UNKNOWN = "1"

LIC_FILES_CHKSUM = "file://license_terms/license_agreement.txt;md5=1a33828e132ba71861c11688dbb0bd16 \
                    file://license_terms/third_party_licenses/third_party_licenses.txt;md5=b9005e55057311e41efe02ccfea8ea72"

COMPATIBLE_HOST = "(aarch64|x86_64).*-linux"

require remove-execstack.inc
REMOVE_EXECSTACKS:x86-64 = "${FVPDIR}/models/${FVP_ARCH_DIR}*/libarmctmodel.so"
