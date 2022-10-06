#
# Whether the platform supports kernel tracing
#
LTTNGMODULES = "lttng-modules"
LTTNGMODULES:arc = ""
LTTNGMODULES:riscv64 = ""

COMPATIBLE_HOST:riscv64:pn-lttng-modules = "null"
COMPATIBLE_HOST:arc:pn-lttng-modules = "null"

# Whether the platform supports userspace tracing
# lttng-ust uses sched_getcpu() which is not there on for some platforms.
LTTNGUST = "lttng-ust"
LTTNGUST:arc = ""

COMPATIBLE_HOST:arc:pn-lttng-ust = "null"

# Whether the platform supports lttng-tools
# lttng-tools requires SYS_ppoll and SYS_pselect6 which are not supported on riscv32.
# It's also turned off for riscv32 in meta-riscv. See https://github.com/riscv/meta-riscv/blob/master/conf/layer.conf
COMPATIBLE_HOST:riscv32:pn-lttng-tools = "null"
