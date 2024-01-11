SUMMARY = "Timezone data"
DESCRIPTION = "The Time Zone Database contains code and data that represent \
the history of local time for many representative locations around the globe."
HOMEPAGE = "http://www.iana.org/time-zones"
SECTION = "base"
LICENSE = "PD & BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c679c9d6b02bc2757b3eaf8f53c43fba"

PV = "2023d"

SRC_URI =" http://www.iana.org/time-zones/repository/releases/tzcode${PV}.tar.gz;name=tzcode;subdir=tz \
           http://www.iana.org/time-zones/repository/releases/tzdata${PV}.tar.gz;name=tzdata;subdir=tz \
           "

S = "${WORKDIR}/tz"

UPSTREAM_CHECK_URI = "http://www.iana.org/time-zones"

SRC_URI[tzcode.sha256sum] = "e9a5f9e118886d2de92b62bb05510a28cc6c058d791c93bd6b84d3292c3c161e"
SRC_URI[tzdata.sha256sum] = "dbca21970b0a8b8c0ceceec1d7b91fa903be0f6eca5ae732b5329672232a08f3"
