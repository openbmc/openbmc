SUMMARY = "A tool to help with creating, uploading, and upgrading Mycroft skills on the skills repo."
HOMEPAGE = "https://github.com/MycroftAI/mycroft-skills-kit"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://README.md;md5=2f90e43663eddf1c33087419fbb35e28"

SRC_URI[md5sum] = "11d9fc865ef627efe68f25fc113974e8"
SRC_URI[sha256sum] = "55be86ff2cd0087016759f2b15b40861cda2a8d8a8d0c669fdacdf32a77a10da"

inherit pypi setuptools3

RDEPENDS_${PN} += "python3-git"
