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
    "
