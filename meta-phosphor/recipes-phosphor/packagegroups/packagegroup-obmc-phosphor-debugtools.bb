SUMMARY = "Phosphor OpenBMC - Debug Tools"
PR = "r1"

inherit packagegroup

RDEPENDS:${PN} = " \
    strace \
    ldd \
    ethtool \
    net-tools \
    phosphor-logging-test \
    lmsensors-sensors \
    tcpdump \
    screen \
    valgrind \
    iperf3 \
    "

# Some older arm architectures don't support valgrind, so explicitly remove
# it as a dependency from them, but keep it by default in anything newer.
RDEPENDS:${PN}:remove:armv5 = "valgrind"
RDEPENDS:${PN}:remove:armv6 = "valgrind"
