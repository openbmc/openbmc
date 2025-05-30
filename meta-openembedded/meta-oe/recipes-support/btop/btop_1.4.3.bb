SUMMARY = "Resource monitor that shows usage and stats for processor, memory, disks, network and processes."
HOMEPAGE = "https://github.com/aristocratos/btop"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"
SECTION = "console/utils"

SRC_URI = "git://github.com/aristocratos/btop.git;protocol=https;branch=main;tag=v${PV} \
           "
SRCREV = "3f1ccd9f6c455fef6d4d8cfbc8ed71b86961323d"

S = "${WORKDIR}/git"

inherit cmake

FILES:${PN} += " \
    ${datadir}/icons \
    ${datadir}/icons/hicolor \
    ${datadir}/icons/hicolor/48x48 \
    ${datadir}/icons/hicolor/scalable \
    ${datadir}/icons/hicolor/48x48/apps \
    ${datadir}/icons/hicolor/48x48/apps/btop.png \
    ${datadir}/icons/hicolor/scalable/apps \
    ${datadir}/icons/hicolor/scalable/apps/btop.svg \
"
