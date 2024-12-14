SUMMARY = "The uncompromising code formatter."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d3465a2a183908c9cb95bf490bd1e7ab \
                    file://docs/license.md;md5=d826b3c5269f21a8a0272af3f00d15c0 \
                    file://src/blib2to3/LICENSE;md5=bbbad7490e921f9a73c0e891305cb4b3"

DEPENDS = "python3-hatch-vcs-native python3-hatchling-native"
SRC_URI[sha256sum] = "846ea64c97afe3bc677b761787993be4991810ecc7a4a937816dd6bddedc4875"

inherit pypi python_hatchling

DEPENDS += "python3-hatch-fancy-pypi-readme-native"

PYPI_PACKAGE = "black"

RDEPENDS:${PN} = "python3-click \
				  python3-mypy-extensions \
				  python3-packaging \
				  python3-pathspec \
				  python3-platformdirs \
				  python3-tomli \
				  python3-typing-extensions \
				  "
