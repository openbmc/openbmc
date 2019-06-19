SUMMARY = "Userspace RCU (read-copy-update) library"
HOMEPAGE = "http://lttng.org/urcu"
BUGTRACKER = "http://lttng.org/project/issues"

LICENSE = "LGPLv2.1+ & MIT-style"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e548d28737289d75a8f1e01ba2fd7825 \
                    file://include/urcu/urcu.h;beginline=4;endline=32;md5=4de0d68d3a997643715036d2209ae1d9 \
                    file://include/urcu/uatomic/x86.h;beginline=4;endline=21;md5=58e50bbd8a2f073bb5500e6554af0d0b"

SRC_URI = "http://lttng.org/files/urcu/userspace-rcu-${PV}.tar.bz2"

SRC_URI[md5sum] = "91c75d5902127f94ab556ce039b369ca"
SRC_URI[sha256sum] = "92b9971bf3f1c443edd6c09e7bf5ff3b43531e778841f16377a812c8feeb3350"

S = "${WORKDIR}/userspace-rcu-${PV}"
inherit autotools multilib_header

CPPFLAGS_append_riscv64  = " -pthread -D_REENTRANT"

do_install_append() {
    oe_multilib_header urcu/config.h
}
