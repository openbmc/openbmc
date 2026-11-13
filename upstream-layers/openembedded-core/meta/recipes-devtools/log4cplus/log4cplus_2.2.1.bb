SUMMARY = "log4cplus provides a simple C++ logging API for log management"
SECTION = "libs"
HOMEPAGE = "https://github.com/log4cplus/log4cplus"
DESCRIPTION = "log4cplus is a simple to use C++ logging API providing thread-safe, flexible, and arbitrarily granular control over log management and configuration. It is modelled after the Java log4j API."
BUGTRACKER = "https://github.com/log4cplus/log4cplus/issues"

LICENSE = "Apache-2.0 AND BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=41e8e060c26822886b592ab4765c756b"

# Replace dots with underscores to match GitHub's tag format (e.g., 2.2.0.1 -> REL_2_2_0_1)
GITHUB_TAG = "REL_${@d.getVar('PV').replace('.', '_')}"

SRC_URI = "https://github.com/log4cplus/log4cplus/releases/download/${GITHUB_TAG}/log4cplus-${PV}.tar.gz"
SRC_URI[sha256sum] = "e19d9b294f8c4c8f60467e0f456e1a347c81d479369d5840afc80c1137b7db6c"

UPSTREAM_CHECK_URI = "https://github.com/log4cplus/log4cplus/releases"
UPSTREAM_CHECK_REGEX = "log4cplus-(?P<pver>\d+(\.\d+)+)\.tar"

inherit autotools pkgconfig

BBCLASSEXTEND = "native"
