require util-linux.inc

SRC_URI += "file://configure-sbindir.patch \
            file://runuser.pamd \
            file://runuser-l.pamd \
            file://ptest.patch \
            file://run-ptest \
            file://display_testname_for_subtest.patch \
            file://avoid_parallel_tests.patch \
            file://0001-hwclock-fix-for-glibc-2.31-settimeofday.patch \
            file://0001-libfdisk-script-accept-sector-size-ignore-unknown-he.patch \
            file://0001-kill-include-sys-types.h-before-checking-SYS_pidfd_s.patch \
            file://0001-include-cleanup-pidfd-inckudes.patch \
"
SRC_URI[md5sum] = "7f64882f631225f0295ca05080cee1bf"
SRC_URI[sha256sum] = "d9de3edd287366cd908e77677514b9387b22bc7b88f45b83e1922c3597f1d7f9"
