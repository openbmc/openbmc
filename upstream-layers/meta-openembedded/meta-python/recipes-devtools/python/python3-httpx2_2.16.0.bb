SUMMARY = "The next generation HTTP client."
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=166cfc32dc0986f87a7e950553b52e5e"

SRC_URI[sha256sum] = "0d54c98128e8f2ff7fbbde0fca8c359e5f032edeaaaac443cdb7f31f685bcd7f"

inherit pypi python_hatchling

DEPENDS += " \
    python3-uv-dynamic-versioning-native \
    python3-hatch-fancy-pypi-readme-native \
    python3-jinja2-native \
    python3-tomlkit-native \
    python3-dunamai-native \
"

RDEPENDS:${PN} += " \
    python3-anyio \
    python3-core \
    python3-h2 \
    python3-httpcore2 \
    python3-idna \
    python3-logging \
    python3-truststore \
"
