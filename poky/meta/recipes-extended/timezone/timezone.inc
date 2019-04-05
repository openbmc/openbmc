SUMMARY = "Timezone data"
HOMEPAGE = "http://www.iana.org/time-zones"
SECTION = "base"
LICENSE = "PD & BSD & BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c679c9d6b02bc2757b3eaf8f53c43fba"

PV = "2019a"

SRC_URI =" http://www.iana.org/time-zones/repository/releases/tzcode${PV}.tar.gz;name=tzcode \
           http://www.iana.org/time-zones/repository/releases/tzdata${PV}.tar.gz;name=tzdata \
           "

UPSTREAM_CHECK_URI = "http://www.iana.org/time-zones"

SRC_URI[tzcode.md5sum] = "27585a20bc5401324f42c8deb6e4677f"
SRC_URI[tzcode.sha256sum] = "8739f162bc30cdfb482435697f969253abea49595541a0afd5f443fbae433ff5"
SRC_URI[tzdata.md5sum] = "288f7b1e43018c633da108f13b27cf91"
SRC_URI[tzdata.sha256sum] = "90366ddf4aa03e37a16cd49255af77f801822310b213f195e2206ead48c59772"
