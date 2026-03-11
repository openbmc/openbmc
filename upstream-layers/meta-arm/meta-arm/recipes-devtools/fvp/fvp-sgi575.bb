require fvp-ecosystem.inc

MODEL = "SGI-575"
MODEL_CODE = "FVP_CSS_SGI-575"
PV = "11.15.26"

SRC_URI[sha256sum] = "d07241112f6c146362deec789e782e10e83bc3560cf605ccd055a606d0b44e74"

# The CSS used in the FVP homepage make it too difficult to query with the tooling currently in Yocto
UPSTREAM_VERSION_UNKNOWN = "1"

LIC_FILES_CHKSUM = "file://license_terms/license_agreement.txt;md5=1a33828e132ba71861c11688dbb0bd16 \
                    file://license_terms/third_party_licenses.txt;md5=3db0c4947b7e3405c40b943672d8de2f"
