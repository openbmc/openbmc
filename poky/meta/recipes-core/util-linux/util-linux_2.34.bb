require util-linux.inc

SRC_URI += "file://configure-sbindir.patch \
            file://runuser.pamd \
            file://runuser-l.pamd \
            file://ptest.patch \
            file://run-ptest \
            file://display_testname_for_subtest.patch \
            file://avoid_parallel_tests.patch \
            file://0001-lsblk-force-to-print-PKNAME-for-partition.patch \
"
SRC_URI[md5sum] = "a78cbeaed9c39094b96a48ba8f891d50"
SRC_URI[sha256sum] = "743f9d0c7252b6db246b659c1e1ce0bd45d8d4508b4dfa427bbb4a3e9b9f62b5"
