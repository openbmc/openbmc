SUMMARY = "Userspace RCU (read-copy-update) library"
HOMEPAGE = "http://lttng.org/urcu"
BUGTRACKER = "http://lttng.org/project/issues"

LICENSE = "LGPLv2.1+ & MIT-style"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e548d28737289d75a8f1e01ba2fd7825 \
                    file://urcu.h;beginline=4;endline=32;md5=4de0d68d3a997643715036d2209ae1d9 \
                    file://urcu/uatomic/x86.h;beginline=4;endline=21;md5=58e50bbd8a2f073bb5500e6554af0d0b"

SRC_URI = " \
	http://lttng.org/files/urcu/userspace-rcu-${PV}.tar.bz2 \
	file://0001-Support-for-NIOS2-architecture.patch \
        file://0002-Support-for-aarch64_be.patch \
	"

SRC_URI[md5sum] = "124eaeea06863271c0bdf2a0cc1d8e4b"
SRC_URI[sha256sum] = "f8d278e9d95bec97c9ba954fc4c3fb584936bc0010713a8fe358b916bafd8715"

S = "${WORKDIR}/userspace-rcu-${PV}"
CFLAGS_append_libc-uclibc = " -D_GNU_SOURCE"
inherit autotools
