SUMMARY = "The next generation HTTP client."
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=166cfc32dc0986f87a7e950553b52e5e"

SRC_URI[sha256sum] = "7631fe9887a8a2275f4a2540e053aa670fcc50742864a9ae7c66e609fdcf12cf"

inherit pypi python_hatchling

DEPENDS += " \
    python3-uv-dynamic-versioning-native \
    python3-hatch-fancy-pypi-readme-native \
    python3-jinja2-native \
    python3-tomlkit-native \
    python3-dunamai-native \
"

RDEPENDS:${PN} += " \
    python3-core \
    python3-logging \
    python3-h2 \
    python3-idna \
"
