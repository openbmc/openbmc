
SUMMARY = "simplelog-topc - Simple, STABLE, powerful of logging library in ANSI C/C++. Ready for billion records."
DESCRIPTION = "Async and Fast C/C++ multi-thread logger with topics. No external dependencies."
HOMEPAGE = "https://github.com/thuanalg/simplelog-topic"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=22cdd382a6275cb4c2e75c517952ac7c"

SRC_URI = "git://github.com/thuanalg/simplelog-topic.git;branch=main;protocol=https;tag=v${PV}"

SRCREV = "edbe423bdd1b36faf30d2eeeb869e4c1755864de"

S = "${WORKDIR}/git"

inherit cmake

# Specify any options you want to pass to cmake using EXTRA_OECMAKE:
EXTRA_OECMAKE += "-DUNIX_LINUX=1"


