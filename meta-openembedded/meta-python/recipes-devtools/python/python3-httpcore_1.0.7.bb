SUMMARY = "A minimal low-level HTTP client."
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=1c1f23b073da202e1f4f9e426490210c"

inherit pypi python_hatchling

SRC_URI[sha256sum] = "8551cb62a169ec7162ac7be8d4817d561f60e08eaa485234898414bb5a8a0b4c"

DEPENDS += "\
    python3-hatch-fancy-pypi-readme-native \
"

PACKAGECONFIG ??= ""
PACKAGECONFIG[http2] = ",,,python3-h2"
PACKAGECONFIG[socks] = ",,,python3-socksio"
PACKAGECONFIG[asyncio] = ",,,python3-anyio"
# Don't provide "trio" PACKAGECONFIG as nothing provides "python3-trio" currently.
# If somebody needs this please feel free to add python3-trio and enable the
# packageconfig below:
#PACKAGECONFIG[trio] = ",,,python3-trio"

RDEPENDS:${PN} += "\
    python3-certifi \
    python3-h11 \
"
