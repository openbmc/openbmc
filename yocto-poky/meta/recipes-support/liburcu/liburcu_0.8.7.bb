SUMMARY = "Userspace RCU (read-copy-update) library"
HOMEPAGE = "http://lttng.org/urcu"
BUGTRACKER = "http://lttng.org/project/issues"

LICENSE = "LGPLv2.1+ & MIT-style"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0f060c30a27922ce9c0d557a639b4fa3 \
                    file://urcu.h;beginline=4;endline=32;md5=4de0d68d3a997643715036d2209ae1d9 \
                    file://urcu/uatomic/x86.h;beginline=4;endline=21;md5=220552f72c55b102f2ee35929734ef42"

SRC_URI = "http://lttng.org/files/urcu/userspace-rcu-${PV}.tar.bz2 \
           file://Revert-Blacklist-ARM-gcc-4.8.0-4.8.1-4.8.2.patch \
           file://aarch64.patch \
           file://0001-uatomic-Specify-complete-types-for-atomic-function-c.patch \
          "

SRC_URI[md5sum] = "7a6ee17871d31226db3f618e28351d22"
SRC_URI[sha256sum] = "b523f22c4726ca6bb77a77d258e76d8c33c89724433bd65313024b98e55c4295"

S = "${WORKDIR}/userspace-rcu-${PV}"
CFLAGS_append_libc-uclibc = " -D_GNU_SOURCE"
inherit autotools
