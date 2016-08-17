#
# Exclude known broken packages
#

# rt-tests needs PI mutex support in libc
EXCLUDE_FROM_WORLD_pn-rt-tests_libc-musl = "1"

# error: no member named 'sin_port' in 'struct sockaddr_in6'
# this is due to libtirpc using ipv6 but portmap rpc expecting ipv4
EXCLUDE_FROM_WORLD_pn-portmap_libc-musl = "1"
EXCLUDE_FROM_WORLD_pn-unfs3_libc-musl = "1"

# error: use of undeclared identifier '_STAT_VER'
EXCLUDE_FROM_WORLD_pn-pseudo_libc-musl = "1"

# error: Need to implement custom I/O
EXCLUDE_FROM_WORLD_pn-libsolv_libc-musl = "1"

# undefined reference to `pthread_tryjoin_np'
EXCLUDE_FROM_WORLD_pn-btrfs-tools_libc-musl = "1"

# error: error.h: No such file or directory
EXCLUDE_FROM_WORLD_pn-prelink_libc-musl = "1"

# error: use of undeclared identifier 'O_CREAT'
EXCLUDE_FROM_WORLD_pn-libbsd_libc-musl = "1"

# error: expected declaration specifiers before '__nonnull'
EXCLUDE_FROM_WORLD_pn-lttng-ust_libc-musl = "1"
EXCLUDE_FROM_WORLD_pn-lttng-tools_libc-musl = "1"

# error: obstack.h: No such file or directory
EXCLUDE_FROM_WORLD_pn-systemtap_libc-musl = "1"
EXCLUDE_FROM_WORLD_pn-systemtap-uprobes_libc-musl = "1"

# error: a parameter list without types is only allowed in a function definition
#            void (*_function)(sigval_t);
EXCLUDE_FROM_WORLD_pn-qemu_libc-musl = "1"

# glibc specific funcrions
# error: storage size of 'mi' isn't known struct mallinfo mi
EXCLUDE_FROM_WORLD_pn-valgrind_libc-musl = "1"

# error: format '%s' expects argument of type 'char *', but argument 4 has type 'int' [-Werror=format=]
#   snprintf(buf, size, "%s", strerror_r(err, sbuf, sizeof(sbuf)));
EXCLUDE_FROM_WORLD_pn-perf_libc-musl = "1"

# error: 'RTLD_NEXT' was not declared in this scope
EXCLUDE_FROM_WORLD_pn-gcc-sanitizers_libc-musl = "1"
