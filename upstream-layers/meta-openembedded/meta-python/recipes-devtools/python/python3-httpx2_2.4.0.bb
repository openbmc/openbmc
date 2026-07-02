SUMMARY = "The next generation HTTP client."
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=166cfc32dc0986f87a7e950553b52e5e"

SRC_URI[sha256sum] = "32e0734b61eb0824b3f56a9e98d6d92d381a3ef12c0045aa917ee63df6c411ef"

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
