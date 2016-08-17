SUMMARY = "Serial Port Support for Python"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=0051a0dc811ee3fdb17a2fd0be44f3ad"

# FIXME might stop packaging serialwin32 and serialjava files

RDEPENDS_${PN} = "\
    ${PYTHON_PN}-fcntl \
    ${PYTHON_PN}-io \
    ${PYTHON_PN}-stringold \
"

SRC_URI[md5sum] = "c8521e49f8852196aac39431e0901703"
SRC_URI[sha256sum] = "969cb6405d1d87f8960cf9c10f597ae58f85da2fb9769dba96f4aeeaade54656"

inherit pypi
