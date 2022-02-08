SUMMARY = "Tooling for devicetree validation using YAML and jsonschema"
AUTHOR = "Rob Herring"

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://setup.py;beginline=2;endline=3;md5=c795d4924c5f739424fa8d9b569c6659"

inherit setuptools3

SRC_URI = "git://github.com/robherring/dt-schema.git;branch=master;protocol=https"
SRCREV = "5009e47c1c76e48871f5988e08dad61f3c91196b"
PV = "0.1+git${SRCPV}"

S = "${WORKDIR}/git"

RDEPENDS_${PN} = "python3-jsonschema python3-ruamel-yaml"
