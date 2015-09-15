SRCREV = "0813335d2e49291e7ab6a4365cec2baa05813eda"
PV = "2.5.3+git${SRCPV}"

inherit pkgconfig

FILESEXTRAPATHS =. "${FILE_DIRNAME}/trace-cmd:"

SRC_URI = "git://git.kernel.org/pub/scm/linux/kernel/git/rostedt/trace-cmd.git;branch=trace-cmd-stable-v2.5 \
           file://blktrace-api-compatibility.patch \
"

S = "${WORKDIR}/git"
