SUMMARY = "A lightweight, object-oriented Python state machine implementation with many extensions."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=de0a0876a688a4483bfafa764773ab39"

inherit pypi setuptools3

SRC_URI[sha256sum] = "2f8490dbdbd419366cef1516032ab06d07ccb5839ef54905e842a472692d4204"

RDEPENDS:${PN} += "python3-six python3-logging"
