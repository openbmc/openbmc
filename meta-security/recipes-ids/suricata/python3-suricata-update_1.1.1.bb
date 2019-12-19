SUMMARY = "The tool for updating your Suricata rules. "
HOMEPAGE = "http://suricata-ids.org/"
SECTION = "security Monitor/Admin"
LICENSE = "GPLv2"

LIC_FILES_CHKSUM = "file://LICENSE;beginline=1;endline=2;md5=c70d8d3310941dcdfcd1e02800a1f548"

SRCREV = "9630630ffc493ca26299d174ee2066aa1405b2d4"
SRC_URI = "git://github.com/OISF/suricata-update;branch='master-1.1.x'"

S = "${WORKDIR}/git"

inherit python3native setuptools3

RDEPENDS_${PN} = "python3-pyyaml"
