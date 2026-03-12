SUMMARY = "Heap memory profiler for Linux"
DESCRIPTION = "Heaptrack traces all memory allocations and annotates these \
events with stack traces. Dedicated analysis tools then allow you to interpret \
the heap memory profile to find hotspots to reduce memory, leaks, allocation \
hotspots and temporary allocations"
HOMEPAGE = "https://phabricator.kde.org/source/heaptrack/"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://LICENSES/Apache-2.0.txt;md5=c846ebb396f8b174b10ded4771514fcc \
                    file://LICENSES/BSD-3-Clause.txt;md5=f225922a2c12dfa5218fb70c49db3ea6 \
                    file://LICENSES/BSL-1.0.txt;md5=4c66a2bad475d1a8f152667a4d0ada34 \
                    file://LICENSES/GPL-2.0-or-later.txt;md5=3d26203303a722dedc6bf909d95ba815 \
                    file://LICENSES/LGPL-2.1-only.txt;md5=41890f71f740302b785c27661123bff5 \
                    file://LICENSES/LGPL-2.1-or-later.txt;md5=147a320ed8b16b036829a0c71d424153 \
                    file://LICENSES/MIT.txt;md5=7dda4e90ded66ab88b86f76169f28663"

DEPENDS = "zlib boost libunwind elfutils"

SRC_URI = "git://github.com/KDE/heaptrack.git;protocol=https;branch=master \
           file://0001-libheaptrack-Fix-build-on-c23.patch \
		  "
SRCREV = "0a15d643791e9829ef3f754ac10e3bed966152c5"
PV .= "+git"

inherit cmake

EXTRA_OECMAKE += "-DHEAPTRACK_BUILD_GUI=OFF"

# libunwind is not yet ported to RISCV32
COMPATIBLE_HOST:riscv32 = "null"

BBCLASSEXTEND = "native"

TOOLCHAIN = "gcc"
