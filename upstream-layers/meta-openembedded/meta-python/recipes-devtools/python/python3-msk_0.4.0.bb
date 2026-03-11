SUMMARY = "A tool to help with creating, uploading, and upgrading Mycroft skills on the skills repo."
HOMEPAGE = "https://github.com/MycroftAI/mycroft-skills-kit"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://README.md;md5=f518abfcfc3935b1f0ec8f2332cb30d3"

SRC_URI[sha256sum] = "f698f193112a8628e776e67b89a95c3f78095857c045dd9cde4cf915a5fbdd80"

inherit pypi setuptools3

RDEPENDS:${PN} += "python3-git"
