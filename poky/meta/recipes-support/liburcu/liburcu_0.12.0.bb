SUMMARY = "Userspace RCU (read-copy-update) library"
HOMEPAGE = "http://lttng.org/urcu"
BUGTRACKER = "http://lttng.org/project/issues"

LICENSE = "LGPLv2.1+ & MIT-style"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e548d28737289d75a8f1e01ba2fd7825 \
                    file://include/urcu/urcu.h;beginline=4;endline=32;md5=4de0d68d3a997643715036d2209ae1d9 \
                    file://include/urcu/uatomic/x86.h;beginline=4;endline=21;md5=58e50bbd8a2f073bb5500e6554af0d0b"

SRC_URI = "http://lttng.org/files/urcu/userspace-rcu-${PV}.tar.bz2"

SRC_URI[md5sum] = "d923a42ce38e33e883313003c8afd559"
SRC_URI[sha256sum] = "409b1be506989e1d26543194df1a79212be990fe5d4fd84f34f019efed989f97"

S = "${WORKDIR}/userspace-rcu-${PV}"
inherit autotools multilib_header

CPPFLAGS_append_riscv64  = " -pthread -D_REENTRANT"

do_install_append() {
    oe_multilib_header urcu/config.h
}
