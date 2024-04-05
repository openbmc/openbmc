SUMMARY = "Userspace support for InfiniBand/RDMA verbs"
DESCRIPTION = "This is the userspace components for the Linux Kernel's drivers Infiniband/RDMA subsystem."
SECTION = "libs"

DEPENDS = "libnl"
RDEPENDS:${PN} = "bash perl"

SRC_URI = "git://github.com/linux-rdma/rdma-core.git;branch=master;protocol=https \
           file://0001-cmake-Allow-SYSTEMCTL_BIN-to-be-overridden-from-envi.patch \
           file://0001-include-libgen.h-for-basename.patch \
"
SRCREV = "bc6b4bc134532e952fe7f8efc251e1f89b912098"
S = "${WORKDIR}/git"

#Default Dual License https://github.com/linux-rdma/rdma-core/blob/master/COPYING.md
LICENSE = "BSD-2-Clause | GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING.BSD_FB;md5=0ec18bae1a9df92c8d6ae01f94a289ae \
		   file://COPYING.GPL2;md5=b234ee4d69f5fce4486a80fdaf4a4263"

EXTRA_OECMAKE = " \
    -DCMAKE_INSTALL_SYSTEMD_SERVICEDIR=${systemd_system_unitdir} \
    -DSYSTEMCTL_BIN=${base_bindir}/systemctl \
    -DCMAKE_INSTALL_PERLDIR=${libdir}/perl5/${@get_perl_version(d)} \
    -DNO_MAN_PAGES=1 \
"

LTO = ""

FILES_SOLIBSDEV = ""
FILES:${PN} += "${libdir}/*"
INSANE_SKIP:${PN} += "dev-so"

inherit cmake cpan-base pkgconfig python3native python3targetconfig systemd

SYSTEMD_SERVICE:${PN} = " \
    srp_daemon.service \
    iwpmd.service \
    ibacm.socket \
    rdma-load-modules@.service \
    srp_daemon_port@.service \
    rdma-hw.target \
    ibacm.service \
"
SYSTEMD_AUTO_ENABLE = "disable"

OECMAKE_FIND_ROOT_PATH_MODE_PROGRAM = "BOTH"
