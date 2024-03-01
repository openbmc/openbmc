SUMMARY = "The Google API Client for Python is a client library for accessing the Plus, \
Moderator, and many other Google APIs."
HOMEPAGE = "https://github.com/googleapis/google-api-python-client"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRC_URI[sha256sum] = "ebf4927a3f5184096647be8f705d090e7f06d48ad82b0fa431a2fe80c2cbe182"

inherit pypi setuptools3

RDEPENDS:${PN} += "\
    python3-logging \
    python3-six \
    python3-json \
    python3-core \
    python3-netclient \
    python3-httplib2 \
    python3-uritemplate \
    python3-google-api-core \
"
