require fvp-ecosystem.inc

MODEL = "TC3"
MODEL_CODE = "FVP_TC3"
PV = "11.26.16"

SRC_URI = "https://developer.arm.com/-/cdn-downloads/permalink/FVPs-Total-Compute/Total-Compute-TC3/${MODEL_CODE}_${PV_URL}_${FVP_ARCH}.tgz;subdir=${BP}"
SRC_URI[sha256sum] = "1d72b508e5d8b50de0e389140b4930e5de53c48ba0d3e0865ed4d15898610069"

# The CSS used in the FVP homepage make it too difficult to query with the tooling currently in Yocto
UPSTREAM_VERSION_UNKNOWN = "1"

LIC_FILES_CHKSUM = "file://license_terms/license_agreement.txt;md5=1a33828e132ba71861c11688dbb0bd16 \
                    file://license_terms/third_party_licenses/third_party_licenses.txt;md5=58b552b918d097a8ba802168312d76b2"
