SUMMARY = "Timezone data"
DESCRIPTION = "The Time Zone Database contains code and data that represent \
the history of local time for many representative locations around the globe."
HOMEPAGE = "http://www.iana.org/time-zones"
SECTION = "base"
LICENSE = "PD & BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c679c9d6b02bc2757b3eaf8f53c43fba"

PV = "2025a"

SRC_URI = "http://www.iana.org/time-zones/repository/releases/tzcode${PV}.tar.gz;name=tzcode;subdir=tz \
           http://www.iana.org/time-zones/repository/releases/tzdata${PV}.tar.gz;name=tzdata;subdir=tz \
           "

S = "${WORKDIR}/tz"

UPSTREAM_CHECK_URI = "http://www.iana.org/time-zones"

SRC_URI[tzcode.sha256sum] = "119679d59f76481eb5e03d3d2a47d7870d592f3999549af189dbd31f2ebf5061"
SRC_URI[tzdata.sha256sum] = "4d5fcbc72c7c450ebfe0b659bd0f1c02fbf52fd7f517a9ea13fe71c21eb5f0d0"
