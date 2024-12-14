SUMMARY = "Invoke py.test as distutils command with dependency resolution"
HOMEPAGE = "https://pypi.org/project/pytest-runner/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7a7126e068206290f3fe9f8d6c713ea6"

SRC_URI[sha256sum] = "70d4739585a7008f37bf4933c013fdb327b8878a5a69fcbb3316c88882f0f49b"

inherit pypi python_setuptools_build_meta

DEPENDS += " \
    python3-setuptools-scm-native"

RDEPENDS:${PN} = "python3-setuptools python3-debugger python3-json python3-io"

BBCLASSEXTEND = "native nativesdk"
