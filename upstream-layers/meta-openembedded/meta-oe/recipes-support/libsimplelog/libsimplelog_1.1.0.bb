
SUMMARY = "simplelog-topc - Simple, STABLE, powerful of logging library in ANSI C/C++. Ready for billion records."
DESCRIPTION = "Async and Fast C/C++ multi-thread logger with topics. No external dependencies."
HOMEPAGE = "https://github.com/thuanalg/simplelog-topic"
LICENSE = "MIT"
#The checksum of "LIC_FILES_CHKSUM" changed b/c I did change from "2024" to "2024-2026"
#libsimplelog  is allways full "MIT license".
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=594fdc3cb85f0b684cf5d7b332a741be"

SRC_URI = "git://github.com/thuanalg/simplelog-topic.git;branch=main;protocol=https;tag=v${PV}"

SRCREV = "151e83902ca9755bcd285791d2f49de6fc30bfe6"

inherit cmake

EXTRA_OECMAKE += "-DUNIX_LINUX=1 -D__LINUX__=1 -D_GNU_SOURCE=1"

# From v.1.0.8 -> v1.1.0
# Note for -D__LINUX__=1 -D_GNU_SOURCE=1
# If don't use these flags, v.1.1.0 works like v1.0.8, and well adapt with musl (strictly POSIX)
# If use 2 flags, they helps the source is more effective. Please test for 2 cases, you will see.
# Please check "p->r = sched_getcpu();" in "simplelog.c"
# You can use as below without any problem
# EXTRA_OECMAKE += "-DUNIX_LINUX=1"

# Drop 0001-allow-build-with-cmake-4.patch as it is now integrated upstream.
# Please see branch "main", commit "4827c4325063266f6d2a7e133a9d3a9050ff6a3c" by
# Author: Alper Ak <alperyasinak1@gmail.com>

