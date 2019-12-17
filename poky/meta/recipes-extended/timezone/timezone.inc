SUMMARY = "Timezone data"
DESCRIPTION = "The Time Zone Database contains code and data that represent \
the history of local time for many representative locations around the globe."
HOMEPAGE = "http://www.iana.org/time-zones"
SECTION = "base"
LICENSE = "PD & BSD & BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c679c9d6b02bc2757b3eaf8f53c43fba"

PV = "2019c"

SRC_URI =" http://www.iana.org/time-zones/repository/releases/tzcode${PV}.tar.gz;name=tzcode \
           http://www.iana.org/time-zones/repository/releases/tzdata${PV}.tar.gz;name=tzdata \
           "

UPSTREAM_CHECK_URI = "http://www.iana.org/time-zones"

SRC_URI[tzcode.md5sum] = "195a17454c5db05cab96595380650391"
SRC_URI[tzcode.sha256sum] = "f6ebd3668e02d5ed223d3b7b1947561bf2d2da2f4bd1db61efefd9e06c167ed4"
SRC_URI[tzdata.md5sum] = "f6987e6dfdb2eb83a1b5076a50b80894"
SRC_URI[tzdata.sha256sum] = "79c7806dab09072308da0e3d22c37d3b245015a591891ea147d3b133b60ffc7c"
