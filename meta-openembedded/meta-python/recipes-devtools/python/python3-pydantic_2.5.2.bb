SUMMARY = "Data validation and settings management using Python type hinting"
HOMEPAGE = "https://github.com/samuelcolvin/pydantic"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=09280955509d1c4ca14bae02f21d49a6"

inherit pypi python_hatchling

SRC_URI[sha256sum] = "ff177ba64c6faf73d7afa2e8cad38fd456c0dbe01c9954e71038001cd15a6edd"

DEPENDS += "python3-hatch-fancy-pypi-readme-native"

RDEPENDS:${PN} += "\
    python3-core \
    python3-datetime \
    python3-image \
    python3-io \
    python3-json \
    python3-logging \
    python3-netclient \
    python3-numbers \
    python3-profile \
    python3-typing-extensions \
"
