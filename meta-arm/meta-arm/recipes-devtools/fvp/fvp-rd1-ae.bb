require fvp-ecosystem.inc

MODEL = "RD-1_AE"
MODEL_CODE = "FVP_RD_1_AE"
PV = "11.27.20"

SRC_URI = "https://developer.arm.com/-/cdn-downloads/permalink/FVPs-Automotive/${MODEL}/${MODEL_CODE}_${PV_URL}_${FVP_ARCH}.tgz;subdir=${BP};name=fvp-${HOST_ARCH}"
SRC_URI[fvp-aarch64.sha256sum] = "297ded55d025772c9ad8497c6a97e0619fc1762dd1236e3ddec14da449f51ca4"
SRC_URI[fvp-x86_64.sha256sum] = "e2b01fafac9cd560ed7a42f155241971d0cef086404c56bbb44dc6c9bf672e7d"

# The CSS used in the FVP homepage make it too difficult to query with the tooling currently in Yocto
UPSTREAM_VERSION_UNKNOWN = "1"

LIC_FILES_CHKSUM = "file://license_terms/license_agreement.txt;md5=1a33828e132ba71861c11688dbb0bd16 \
                    file://license_terms/third_party_licenses/third_party_licenses.txt;md5=a3ce84371977a6b9c624408238309a90"

COMPATIBLE_HOST = "(aarch64|x86_64).*-linux"
