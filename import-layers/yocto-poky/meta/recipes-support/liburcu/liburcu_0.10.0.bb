SUMMARY = "Userspace RCU (read-copy-update) library"
HOMEPAGE = "http://lttng.org/urcu"
BUGTRACKER = "http://lttng.org/project/issues"

LICENSE = "LGPLv2.1+ & MIT-style"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e548d28737289d75a8f1e01ba2fd7825 \
                    file://src/urcu.h;beginline=4;endline=32;md5=4de0d68d3a997643715036d2209ae1d9 \
                    file://include/urcu/uatomic/x86.h;beginline=4;endline=21;md5=58e50bbd8a2f073bb5500e6554af0d0b"

SRC_URI = "http://lttng.org/files/urcu/userspace-rcu-${PV}.tar.bz2 \
           "

SRC_URI[md5sum] = "69dab85b6929c378338b9504adc6aea7"
SRC_URI[sha256sum] = "7cb58a7ba5151198087f025dc8d19d8918e9c6d56772f039696c111d9aad3190"

S = "${WORKDIR}/userspace-rcu-${PV}"
inherit autotools
