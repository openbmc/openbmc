SUMMARY = "Real-time and non-real-time traffic validation tool for converged TSN networks"

DESCRIPTION = "The Linux RealTime Communication Testbench validates real-time \
performance and robustness of hardware, drivers, and the Linux network stack on \
TSN-enabled Ethernet networks. It generates and mirrors cyclic traffic using \
AF_PACKET or AF_XDP with eBPF, supporting protocols like PROFINET and OPC UA PubSub."

HOMEPAGE = "https://github.com/Linutronix/RTC-Testbench"
LICENSE = "BSD-2-Clause & (GPL-2.0-only | BSD-2-Clause)"
LIC_FILES_CHKSUM = " \
    file://LICENSE;md5=f39e57686080f8752e19c4cd3e04e351 \
    file://LICENSES/BSD-2-Clause.txt;md5=9e16594a228301089d759b4f178db91f \
    file://LICENSES/GPL-2.0-only.txt;md5=3d26203303a722dedc6bf909d95ba815 \
"
SRC_URI = " \
    git://github.com/Linutronix/RTC-Testbench.git;tag=v${PV};nobranch=1;protocol=https \
    file://0001-CMakeLists.txt-make-BPF-clang-and-include-paths-conf.patch \
"
SRCREV = "bf016fdf422094f1ef65c0d88f148f46663ebbd8"

DEPENDS += " \
    libyaml \
    libbpf \
    xdp-tools \
    openssl \
    clang-native \
"

inherit cmake pkgconfig

EXTRA_OECMAKE += " \
    -DRX_TIMESTAMP=ON \
    -DTX_TIMESTAMP=ON \
    -DCLANG=${STAGING_BINDIR_NATIVE}/clang \
    -DBPF_INCLUDE_DIRS=${STAGING_INCDIR} \
    -DBPF_EXTRA_FLAGS='-D__${TARGET_ARCH}__ -ffile-prefix-map=${S}=${TARGET_DBGSRC_DIR} -ffile-prefix-map=${STAGING_DIR_HOST}=' \
"

# Require bash since installed scripts use /bin/bash shebang
RDEPENDS:${PN} += "bash"
