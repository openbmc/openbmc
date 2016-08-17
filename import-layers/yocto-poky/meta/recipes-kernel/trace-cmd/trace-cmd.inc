SRCREV = "9be5d74805830a291615f2f34a27c903f6a37b1e"
PV = "2.6"

inherit pkgconfig

FILESEXTRAPATHS =. "${FILE_DIRNAME}/trace-cmd:"

SRC_URI = "git://git.kernel.org/pub/scm/linux/kernel/git/rostedt/trace-cmd.git;branch=trace-cmd-stable-v2.6 \
           file://blktrace-api-compatibility.patch \
"

S = "${WORKDIR}/git"
