SUMMARY = "Userspace support for InfiniBand/RDMA verbs"
DESCRIPTION = "This is the userspace components for the Linux Kernel's drivers Infiniband/RDMA subsystem."
SECTION = "libs"

DEPENDS = "libnl"
RDEPENDS_${PN} = "bash perl"

BRANCH = "stable-v${@d.getVar('PV').split('.')[0]}"
SRC_URI = "git://github.com/linux-rdma/rdma-core.git;branch=${BRANCH} \
           file://0001-Remove-man-files-which-cant-be-built.patch \
           "
SRCREV = "f12c953f0864691eacc9fcc4cda489b92ffd5a85"
S = "${WORKDIR}/git"

#Default Dual License https://github.com/linux-rdma/rdma-core/blob/master/COPYING.md
LICENSE = "BSD-2-Clause | GPLv2"
LIC_FILES_CHKSUM = "file://COPYING.BSD_FB;md5=0ec18bae1a9df92c8d6ae01f94a289ae \
		   file://COPYING.GPL2;md5=b234ee4d69f5fce4486a80fdaf4a4263"

EXTRA_OECMAKE = " \
    -DCMAKE_INSTALL_SYSTEMD_SERVICEDIR=${systemd_system_unitdir} \
    -DCMAKE_INSTALL_PERLDIR=${libdir}/perl5/${@get_perl_version(d)} \
"

FILES_SOLIBSDEV = ""
FILES_${PN} += "${libdir}/*"
INSANE_SKIP_${PN} += "dev-so"

inherit cmake cpan-base systemd

SYSTEMD_SERVICE_${PN} = " \
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
