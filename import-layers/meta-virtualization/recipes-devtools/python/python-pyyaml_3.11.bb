HOMEPAGE = "http://www.pyyaml.org"
SUMMARY = "Python support for YAML"
DESCRIPTION = "\
  YAML is a data serialization format designed for human readability \
  and interaction with scripting languages.  PyYAML is a YAML parser \
  and emitter for Python. \
  .       \
  PyYAML features a complete YAML 1.1 parser, Unicode support, pickle \
  support, capable extension API, and sensible error messages.  PyYAML \
  supports standard YAML tags and provides Python-specific tags that \
  allow to represent an arbitrary Python object. \
  .       \
  PyYAML is applicable for a broad range of tasks from complex \
  configuration files to object serialization and persistance. \
  "
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6015f088759b10e0bc2bf64898d4ae17"

SRCNAME = "PyYAML"
SRC_URI = "http://pyyaml.org/download/pyyaml/${SRCNAME}-${PV}.tar.gz"

SRC_URI[md5sum] = "f50e08ef0fe55178479d3a618efe21db"
SRC_URI[sha256sum] = "c36c938a872e5ff494938b33b14aaa156cb439ec67548fcab3535bb78b0846e8"

S = "${WORKDIR}/${SRCNAME}-${PV}"

inherit setuptools

DEPENDS += "libyaml python-cython-native"
