SUMMARY = "A tool to help with creating, uploading, and upgrading Mycroft skills on the skills repo."
HOMEPAGE = "https://github.com/MycroftAI/mycroft-skills-kit"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://README.md;md5=2f90e43663eddf1c33087419fbb35e28"

SRC_URI[md5sum] = "dcb286c489d24d8b0fb8413835f7478a"
SRC_URI[sha256sum] = "56d3557889ee2ceebc72284f979aa6ddd4c7fbe2af31142eb6f51404f14516d5"

inherit pypi setuptools3

RDEPENDS_${PN} += "python3-git"
