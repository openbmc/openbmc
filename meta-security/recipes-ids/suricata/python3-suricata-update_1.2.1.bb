SUMMARY = "The tool for updating your Suricata rules. "
HOMEPAGE = "http://suricata-ids.org/"
SECTION = "security Monitor/Admin"
LICENSE = "GPLv2"

LIC_FILES_CHKSUM = "file://LICENSE;beginline=1;endline=2;md5=c70d8d3310941dcdfcd1e02800a1f548"

SRCREV = "50e857f75e576e239d8306a6ac55946a1ce252a6"
SRC_URI = "git://github.com/OISF/suricata-update;branch='master-1.2.x'"

S = "${WORKDIR}/git"

inherit python3native python3targetconfig setuptools3

RDEPENDS_${PN} = "python3-pyyaml python3-logging python3-compression"

BBCLASSEXTEND = "native nativesdk"
