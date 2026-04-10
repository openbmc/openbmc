require fvp-ecosystem.inc

MODEL = "RD-V1"
MODEL_CODE = "FVP_RD_V1"
PV = "11.17.29"

SRC_URI = "https://developer.arm.com/-/cdn-downloads/permalink/FVPs-Neoverse-Infrastructure/${MODEL}/${MODEL_CODE}_${PV_URL}_${FVP_ARCH}.tgz;subdir=${BP};name=fvp-${HOST_ARCH}"
SRC_URI[fvp-x86_64.sha256sum] = "715c0e0264fc3df961a0b9ff4c4399d55e4ccf4efb0944a412141d97873027a3"

# The CSS used in the FVP homepage make it too difficult to query with the tooling currently in Yocto
UPSTREAM_VERSION_UNKNOWN = "1"

LIC_FILES_CHKSUM = "file://license_terms/license_agreement.txt;md5=1a33828e132ba71861c11688dbb0bd16 \
                    file://license_terms/third_party_licenses.txt;md5=41029e71051b1c786bae3112a29905a7"
