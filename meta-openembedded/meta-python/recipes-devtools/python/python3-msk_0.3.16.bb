SUMMARY = "A tool to help with creating, uploading, and upgrading Mycroft skills on the skills repo."
HOMEPAGE = "https://github.com/MycroftAI/mycroft-skills-kit"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://README.md;md5=2f90e43663eddf1c33087419fbb35e28"

SRC_URI[md5sum] = "8a4fc92a074544f81f482da2c21ef989"
SRC_URI[sha256sum] = "c6a717fc068f7c69ddc8cb21dbeeda7cfa97a4e9f41690459c9fbec68b16ee87"

inherit pypi setuptools3

RDEPENDS_${PN} += "python3-git"
