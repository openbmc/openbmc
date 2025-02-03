SUMMARY = "Userspace RCU (read-copy-update) library"
DESCRIPTION = "A userspace RCU (read-copy-update) library. This data \
synchronization library provides read-side access which scales linearly \
with the number of cores. "
HOMEPAGE = "http://lttng.org/urcu"
BUGTRACKER = "http://lttng.org/project/issues"

LICENSE = "LGPL-2.1-or-later & MIT"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=c2a92498b6e88e276f986877995425b8 \
                    file://include/urcu/urcu.h;beginline=1;endline=18;md5=d35fe8cc2613ca70e0a624ed8bf6fef9 \
                    file://include/urcu/uatomic/x86.h;beginline=1;endline=6;md5=358d69272ba7b5f85e29e342430d440c \
                    "


SRC_URI = "http://lttng.org/files/urcu/userspace-rcu-${PV}.tar.bz2"

SRC_URI[sha256sum] = "4f2d839af67905ad396d6d53ba5649b66113d90840dcbc89941e0da64bccd38c"

S = "${WORKDIR}/userspace-rcu-${PV}"
inherit autotools multilib_header

CPPFLAGS:append:riscv64  = " -pthread -D_REENTRANT"

do_install:append() {
    oe_multilib_header urcu/config.h
}
