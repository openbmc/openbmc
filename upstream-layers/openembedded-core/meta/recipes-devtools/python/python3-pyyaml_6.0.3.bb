SUMMARY = "Python support for YAML"
HOMEPAGE = "https://pyyaml.org/"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6d8242660a8371add5fe547adf083079"

SRC_URI[sha256sum] = "d76623373421df22fb4cf8817020cbb7ef15c725b9d5e45f17e189bfc384190f"

SRC_URI += "\
    https://raw.githubusercontent.com/yaml/pyyaml/a98fd6088e81d7aca571220c966bbfe2ac43c335/tests/test_dump_load.py;name=test \
"
SRC_URI[test.sha256sum] = "b6a8a2825d89fdc8aee226560f66b8196e872012a0ea7118cbef1a832359434a"

inherit pypi python_setuptools_build_meta ptest-python-pytest cython

PACKAGECONFIG ?= "libyaml"
PACKAGECONFIG[libyaml] = "--with-libyaml,--without-libyaml,libyaml"

RDEPENDS:${PN} += "\
    python3-datetime \
    python3-netclient \
"

BBCLASSEXTEND = "native nativesdk"
