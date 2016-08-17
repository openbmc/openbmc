SUMMARY = "A set of useful command line tools"
SUMMARY_${PN}-debug = "A set of command line tools useful for debugging"
SECTION = "console"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"
PV = "1.0"
PR = "r21"

inherit packagegroup allarch

PACKAGES += "${PN}-debug"

RDEPENDS_${PN} = "\
    dbus-daemon-proxy \
    dosfstools \
    htop \
    iptables \
    lsof \
    mbuffer \
    mtd-utils \
    nano \
    nfs-utils-client \
    nmon \
    powertop \
    screen \
    socat \
    sysstat \
"

RDEPENDS_${PN}-debug = "\
    evtest \
    devmem2 \
    i2c-tools \
    gdb \
    procps \
    pxaregs \
    s3c24xx-gpio \
    s3c64xx-gpio \
    serial-forward \
    strace \
"

RRECOMMENDS_${PN}-debug = "\
    ltrace \
"
