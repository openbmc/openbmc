SUMMARY = "Userspace RCU (read-copy-update) library"
DESCRIPTION = "A userspace RCU (read-copy-update) library. This data \
synchronization library provides read-side access which scales linearly \
with the number of cores. "
HOMEPAGE = "http://lttng.org/urcu"
BUGTRACKER = "http://lttng.org/project/issues"

LICENSE = "LGPLv2.1+ & MIT-style"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e548d28737289d75a8f1e01ba2fd7825 \
                    file://include/urcu/urcu.h;beginline=4;endline=32;md5=4de0d68d3a997643715036d2209ae1d9 \
                    file://include/urcu/uatomic/x86.h;beginline=4;endline=21;md5=58e50bbd8a2f073bb5500e6554af0d0b"

SRC_URI = "http://lttng.org/files/urcu/userspace-rcu-${PV}.tar.bz2"

SRC_URI[md5sum] = "8cb75dbb05774c03e66c63cb3186dd59"
SRC_URI[sha256sum] = "cbb20dbe1a892c2a4d8898bac4316176e585392693d498766ccbbc68cf20ba20"

S = "${WORKDIR}/userspace-rcu-${PV}"
inherit autotools multilib_header

CPPFLAGS:append:riscv64  = " -pthread -D_REENTRANT"

do_install:append() {
    oe_multilib_header urcu/config.h
}
