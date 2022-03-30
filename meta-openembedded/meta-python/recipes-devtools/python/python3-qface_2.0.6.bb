SUMMARY = "A generator framework based on a common modern IDL"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=eee61e10a40b0e3045ee5965bcd9a8b5"

SRC_URI[sha256sum] = "87ce8ce7123ae57d91ac903c6c330a99273e7f4665a0ab88e6a17f9c98143a68"

inherit pypi python_setuptools_build_meta

DEPS = "\
    python3-jinja2 \
    python3-click \
    python3-pyyaml \
    python3-pytest \
    python3-six \
    python3-path \
    python3-antlr4-runtime \
    python3-watchdog \
    python3-markupsafe \
    python3-setuptools \
"
DEPENDS += "${DEPS}"
RDEPENDS:${PN} += "${DEPS}"

BBCLASSEXTEND = "nativesdk native"
