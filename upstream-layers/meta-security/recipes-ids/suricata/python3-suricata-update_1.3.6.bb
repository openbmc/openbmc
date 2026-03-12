SUMMARY = "The tool for updating your Suricata rules"
HOMEPAGE = "http://suricata-ids.org/"
SECTION = "security Monitor/Admin"
LICENSE = "GPL-2.0-only"

LIC_FILES_CHKSUM = "file://LICENSE;beginline=1;endline=2;md5=c70d8d3310941dcdfcd1e02800a1f548"

SRCREV = "f76a61bdee69961537585a1036c4294da28495a5"
SRC_URI = "git://github.com/OISF/suricata-update;protocol=https;branch=master"

inherit python3native python3targetconfig setuptools3

RDEPENDS:${PN} = "python3-pyyaml python3-logging python3-compression python3-shell"

BBCLASSEXTEND = "native nativesdk"
