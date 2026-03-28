require fvp-ecosystem.inc

MODEL = "RD-1_AE"
MODEL_CODE = "FVP_RD_1_AE"
PV = "11.29.27"

SRC_URI = "https://developer.arm.com/-/cdn-downloads/permalink/FVPs-Automotive/${MODEL}/${MODEL_CODE}_${PV_URL}_${FVP_ARCH}.tgz;subdir=${BP};name=fvp-${HOST_ARCH}"
SRC_URI[fvp-aarch64.sha256sum] = "4801691619edeadb861bd8ede783264fc492c83525af4739e8c122a8a96e850a"
SRC_URI[fvp-x86_64.sha256sum] = "fefdfdb495f72d6187a03cee31d0d87ebd85ce8bcac69a006861999f0452e3a7"

# The CSS used in the FVP homepage make it too difficult to query with the tooling currently in Yocto
UPSTREAM_VERSION_UNKNOWN = "1"

LIC_FILES_CHKSUM = "file://license_terms/license_agreement.txt;md5=1a33828e132ba71861c11688dbb0bd16 \
                    file://license_terms/third_party_licenses/third_party_licenses.txt;md5=92ea58bb219213dfdc48111b693019b6"

COMPATIBLE_HOST = "(aarch64|x86_64).*-linux"
