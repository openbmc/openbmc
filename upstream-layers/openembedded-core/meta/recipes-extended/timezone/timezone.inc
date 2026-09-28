SUMMARY = "Timezone data"
DESCRIPTION = "The Time Zone Database contains code and data that represent \
the history of local time for many representative locations around the globe."
HOMEPAGE = "http://www.iana.org/time-zones"
SECTION = "base"
LICENSE = "PD & BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c679c9d6b02bc2757b3eaf8f53c43fba"

PV = "2026c"

SRC_URI = "http://www.iana.org/time-zones/repository/releases/tzcode${PV}.tar.gz;name=tzcode;subdir=tz \
           http://www.iana.org/time-zones/repository/releases/tzdata${PV}.tar.gz;name=tzdata;subdir=tz \
           "

S = "${UNPACKDIR}/tz"

UPSTREAM_CHECK_URI = "http://www.iana.org/time-zones"

SRC_URI[tzcode.sha256sum] = "b1cffc3ace4c4c7cd0efba2f7add86ec3d0b79da48bcf03582671fd3c8feace8"
SRC_URI[tzdata.sha256sum] = "e4a178a4477f3d0ea77cc31828ff72aa38feff8d61aa13e7e99e142e9d902be4"
