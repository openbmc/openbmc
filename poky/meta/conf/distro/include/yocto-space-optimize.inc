# Some components have debug data and workdirs that are simply too large to be useful
# in general builds. Disable or reduce debugging for these cases to improve disk usage
# and build speed.

# With -g, llvm workdir is 40GB, llvm-staticdev is 1.3GB, llvm-dbg is 1GB
DEBUG_LEVELFLAG:pn-llvm = ""
# With -g, qemu workdir is 10GB, qemu-dbg is 363MB
DEBUG_LEVELFLAG:pn-qemu = ""
