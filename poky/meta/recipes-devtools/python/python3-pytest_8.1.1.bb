SUMMARY = "Simple powerful testing with python"
HOMEPAGE = "https://pypi.org/project/pytest/"
DESCRIPTION = "The pytest framework makes it easy to write small tests, yet scales to support complex functional testing for applications and libraries."

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=bd27e41b6550fe0fc45356d1d81ee37c"

SRC_URI[sha256sum] = "ac978141a75948948817d360297b7aae0fcb9d6ff6bc9ec6d514b85d5a65c044"

DEPENDS += "python3-setuptools-scm-native"

inherit update-alternatives pypi python_setuptools_build_meta

RDEPENDS:${PN} += " \
    python3-atomicwrites \
    python3-attrs \
    python3-debugger \
    python3-doctest \
    python3-importlib-metadata \
    python3-iniconfig \
    python3-json \
    python3-more-itertools \
    python3-packaging \
    python3-pathlib2 \
    python3-pluggy \
    python3-py \
    python3-setuptools \
    python3-six \
    python3-tomllib \
    python3-wcwidth \
"

ALTERNATIVE:${PN} += "py.test pytest"

NATIVE_LINK_NAME[pytest] = "${bindir}/pytest"
ALTERNATIVE_TARGET[pytest] = "${bindir}/pytest"

ALTERNATIVE_LINK_NAME[py.test] = "${bindir}/py.test"
ALTERNATIVE_TARGET[py.test] = "${bindir}/py.test"

BBCLASSEXTEND = "native nativesdk"
