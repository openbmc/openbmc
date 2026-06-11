SUMMARY = "The uncompromising code formatter."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d3465a2a183908c9cb95bf490bd1e7ab \
                    file://docs/license.md;md5=d826b3c5269f21a8a0272af3f00d15c0 \
                    file://src/blib2to3/LICENSE;md5=bbbad7490e921f9a73c0e891305cb4b3"

SRC_URI[sha256sum] = "dd321f668053961824bcc1be1cc1df748b2d7e4fa28086b08331e577b0100a73"

inherit pypi python_hatchling

DEPENDS += " \
    python3-hatch-vcs-native \
    python3-hatchling-native \
    python3-hatch-fancy-pypi-readme-native \
"

RDEPENDS:${PN} = " \
    python3-click \
    python3-mypy-extensions \
    python3-packaging \
    python3-pathspec \
    python3-platformdirs \
    python3-tomli \
    python3-typing-extensions \
    python3-pytokens \
"
