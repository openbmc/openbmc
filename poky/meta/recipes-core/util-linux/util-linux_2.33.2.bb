require util-linux.inc

SRC_URI += "file://configure-sbindir.patch \
            file://runuser.pamd \
            file://runuser-l.pamd \
            file://ptest.patch \
            file://run-ptest \
            file://display_testname_for_subtest.patch \
            file://avoid_parallel_tests.patch \
            file://check-for-_HAVE_STRUCT_TERMIOS_C_OSPEED.patch \
"
SRC_URI[md5sum] = "91653b90fcbe9c161153e39b8cc69fb5"
SRC_URI[sha256sum] = "631be8eac6cf6230ba478de211941d526808dba3cd436380793334496013ce97"
