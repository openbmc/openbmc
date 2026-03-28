SUMMARY = "The uncompromising code formatter."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d3465a2a183908c9cb95bf490bd1e7ab \
                    file://docs/license.md;md5=d826b3c5269f21a8a0272af3f00d15c0 \
                    file://src/blib2to3/LICENSE;md5=bbbad7490e921f9a73c0e891305cb4b3"

SRC_URI[sha256sum] = "2c50f5063a9641c7eed7795014ba37b0f5fa227f3d408b968936e24bc0566b07"

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
