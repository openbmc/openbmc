SUMMARY = "Data validation and settings management using Python type hinting"
HOMEPAGE = "https://github.com/samuelcolvin/pydantic"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=09280955509d1c4ca14bae02f21d49a6"

inherit pypi python_hatchling

SRC_URI[sha256sum] = "b172505886028e4356868d617d2d1a776d7af1625d1313450fd51bdd19d9d61f"

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
