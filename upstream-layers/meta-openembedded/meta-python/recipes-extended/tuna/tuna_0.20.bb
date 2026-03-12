SUMMARY = "cui/gui tool for tuning of running processes"
HOMEPAGE = "https://rt.wiki.kernel.org/index.php/Tuna"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0-only;md5=801f80980d171dd6425610833a22dbe6"

SRC_URI = "git://git.kernel.org/pub/scm/utils/tuna/tuna.git;branch=main"

SRCREV = "596d92cac33e64ec9b0176401395e6a4abc2266a"


RDEPENDS:${PN} += " \
    python3-io \
    python3-linux-procfs \
    python3-logging \
    python3-six \
    "

inherit python_setuptools_build_meta

do_install:append() {
    install -m 0755 -d ${D}${bindir}
    install -m 0755 ${S}/tuna-cmd.py ${D}${bindir}/tuna
}
