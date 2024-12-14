SUMMARY = "Python tool to parse, validate and convert spdx files"
HOMEPAGE = "https://github.com/spdx/tools-python"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=dc7f21ccff0f672f2a7cd6f412ae627d"

SRC_URI[sha256sum] = "68b8f9ce2893b5216bd90b2e63f1c821c2884e4ebc4fd295ebbf1fa8b8a94b93"

BBCLASSEXTEND = "native nativesdk"

inherit pypi python_setuptools_build_meta

DEPENDS += "python3-setuptools-scm-native"

# Dependency required for pyspdxtools : python3-click
# Dependencies required for conversion to spdx3 : python3-semantic-version, python3-ply
RDEPENDS:${PN} += "\
  python3-core \
  python3-beartype \
  python3-click \
  python3-datetime \
  python3-json \
  python3-license-expression \
  python3-ply \
  python3-pyyaml \
  python3-rdflib \
  python3-semantic-version \
  python3-uritools \
  python3-xmltodict \
  "
