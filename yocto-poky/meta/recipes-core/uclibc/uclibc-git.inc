SRCREV = "003b266cbeb370a8eae91dc256197f00798c6f93"

PV = "1.0.12+git${SRCPV}"

FILESEXTRAPATHS =. "${FILE_DIRNAME}/uclibc-git:"

SRC_URI = "git://uclibc-ng.org/git/uclibc-ng \
        file://0001-Disable-lrount_tes-function.patch \
        file://uClibc.machine \
        file://uClibc.distro \
        file://obstack.cfg \
        file://locale.cfg \
"
S = "${WORKDIR}/git"

UPSTREAM_CHECK_GITTAGREGEX = "(?P<pver>\d+\.\d+(\.\d+)*)"

