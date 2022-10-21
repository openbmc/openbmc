SUMMARY = "Double conversion libraries"
DESCRIPTION = "This provides binary-decimal and decimal-binary routines for IEEE doubles."
HOMEPAGE = "https://github.com/google/double-conversion.git"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1ea35644f0ec0d9767897115667e901f"


S = "${WORKDIR}/git"

SRC_URI = " \
        git://github.com/google/double-conversion.git;protocol=https;branch=master \
"
SRCREV = "af09fd65fcf24eee95dc62813ba9123414635428"

inherit cmake

EXTRA_OECMAKE = "-DBUILD_SHARED_LIBS=ON"
