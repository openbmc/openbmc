SUMMARY = "SBE log python parsers"
DESCRIPTION = "Used by peltool to parse SBE UserData sections and SRC details"
HOMEPAGE = "https://github.ibm.com/open-power/sbe"

PR = "r1"
PV = "1.0+git${SRCPV}"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE_PROLOG;md5=d8e5f403c98fd80dcea90b9cc8cd083c"

S = "${WORKDIR}/git"
SRC_URI = "git://git@github.com/open-power/sbe;branch="master-p10";protocol=ssh;protocol=https"
SRCREV = "f8ee8cfad2cf2a53e78789633ddffc192f5a197e"

inherit setuptools3
