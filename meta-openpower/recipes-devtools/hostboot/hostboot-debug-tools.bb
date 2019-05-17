SUMMARY = "Recipe for enabling hostboot-debug-tools"
DESCRIPTION = "Support to enable running hostboot-debug-tools"
LICENSE = "Apache-2.0"
PR = "r1"

inherit allarch

RDEPENDS_${PN} = " \
    perl perl-module-pod-text perl-module-lib \
    perl-module-constant \
    perl-module-errno perl-module-fcntl \
    perl-module-file-basename perl-module-file-copy \
    perl-module-file-find perl-module-file-glob \
    perl-module-file-path perl-module-file-spec \
    perl-module-file-temp perl-module-getopt-long \
    perl-module-re perl-module-text-wrap \
    perl-module-posix perl-module-bigint \
    perl-module-integer \
    "

ALLOW_EMPTY_${PN} = "1"
