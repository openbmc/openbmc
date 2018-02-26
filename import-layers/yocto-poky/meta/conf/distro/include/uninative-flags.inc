# Some distros (ubuntu 16.10, debian-testing) default to gcc configured with
# --enable-default-pie (see gcc -v). This breaks e.g. prelink-native on a pie
# default system if binutils-native was built on a system which is not pie default
# We therefore enable pie unconditionally for native recipes where static libs are
# used such as libiberty from binutils, for now, until our minimum distro set is
# all default pie.
BUILD_CFLAGS_append_pn-binutils-native = " -pie -fpie"
