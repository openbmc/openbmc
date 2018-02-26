SRCREV = "021710e1073fe203341b427cd1a4bac577ec899c"
PV = "2.6.1"

inherit pkgconfig

FILESEXTRAPATHS =. "${FILE_DIRNAME}/trace-cmd:"

SRC_URI = "git://git.kernel.org/pub/scm/linux/kernel/git/rostedt/trace-cmd.git;branch=trace-cmd-stable-v2.6 \
           file://blktrace-api-compatibility.patch \
           file://0001-Include-limits.h-so-that-PATH_MAX-is-defined-an-issu.patch \
"

S = "${WORKDIR}/git"
