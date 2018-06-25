SRCREV = "7d0147bbba3ed1d5ef6eea4eec3f0ad4c98f02b5"
PV = "2.7"

inherit pkgconfig

FILESEXTRAPATHS =. "${FILE_DIRNAME}/trace-cmd:"

SRC_URI = "git://git.kernel.org/pub/scm/linux/kernel/git/rostedt/trace-cmd.git \
           file://blktrace-api-compatibility.patch \
           file://0001-Include-limits.h-so-that-PATH_MAX-is-defined-an-issu.patch \
"

S = "${WORKDIR}/git"
