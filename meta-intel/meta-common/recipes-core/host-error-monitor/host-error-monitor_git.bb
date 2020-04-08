LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7becf906c8f8d03c237bad13bc3dac53"
inherit cmake systemd

SRC_URI = "git://github.com/openbmc/host-error-monitor"

DEPENDS = "boost sdbusplus libgpiod libpeci"

PV = "0.1+git${SRCPV}"
SRCREV = "53099c49b89c22cfe09c7217679ae1fb20e019eb"

S = "${WORKDIR}/git"

SYSTEMD_SERVICE_${PN} += "xyz.openbmc_project.HostErrorMonitor.service"
SECURITY_CFLAGS_pn-host-error-monitor  = "${SECURITY_NOPIE_CFLAGS}"

# linux-libc-headers guides this way to include custom uapi headers
CFLAGS_append = " -I ${STAGING_KERNEL_DIR}/include/uapi"
CFLAGS_append = " -I ${STAGING_KERNEL_DIR}/include"
CXXFLAGS_append = " -I ${STAGING_KERNEL_DIR}/include/uapi"
CXXFLAGS_append = " -I ${STAGING_KERNEL_DIR}/include"
do_configure[depends] += "virtual/kernel:do_shared_workdir"
