require util-linux.inc

SRC_URI += "file://configure-sbindir.patch \
            file://runuser.pamd \
            file://runuser-l.pamd \
            file://ptest.patch \
            file://run-ptest \
            file://display_testname_for_subtest.patch \
            file://avoid_parallel_tests.patch \
            file://0001-include-cleanup-pidfd-inckudes.patch \
"
SRC_URI[md5sum] = "248a4d0810c9193e0e9a4bb3f26b93d8"
SRC_URI[sha256sum] = "21b7431e82f6bcd9441a01beeec3d57ed33ee948f8a5b41da577073c372eb58a"
