SUMMARY = "Userspace support for InfiniBand/RDMA verbs"
DESCRIPTION = "This is the userspace components for the Linux Kernel's drivers Infiniband/RDMA subsystem."
SECTION = "libs"

DEPENDS = "libnl"
RDEPENDS_${PN} = "bash perl"

SRC_URI = "git://github.com/linux-rdma/rdma-core.git;branch=stable-v18 \
           file://0001-S_IFSOCK-is-defined-in-both-glibc-musl.patch \
           file://0002-neigh.c-Do-not-include-net-if_packet.h.patch \
           file://0001-include-endian.h-for-htole32-and-friends.patch \
           file://0002-Remove-unused-include-for-execinfo.h.patch \
           file://0001-Remove-man-files-which-cant-be-built.patch \
           "
SRCREV = "7844b3fbe5120623d63b29ecb43eb83a61129658"
S = "${WORKDIR}/git"

#Default Dual License https://github.com/linux-rdma/rdma-core/blob/master/COPYING.md
LICENSE = "BSD-2-Clause | GPLv2"
LIC_FILES_CHKSUM = "file://COPYING.BSD_FB;md5=0ec18bae1a9df92c8d6ae01f94a289ae \
		   file://COPYING.GPL2;md5=b234ee4d69f5fce4486a80fdaf4a4263"


FILES_SOLIBSDEV = ""
FILES_${PN} += "${libdir}/*"
INSANE_SKIP_${PN} += "dev-so"

inherit cmake

OECMAKE_FIND_ROOT_PATH_MODE_PROGRAM = "BOTH"
