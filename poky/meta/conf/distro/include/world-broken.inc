#
# Exclude known broken packages
#

# rt-tests needs PI mutex support in libc
EXCLUDE_FROM_WORLD_pn-rt-tests_libc-musl = "1"

# error: use of undeclared identifier '_STAT_VER'
EXCLUDE_FROM_WORLD_pn-pseudo_libc-musl = "1"

# error: error.h: No such file or directory
EXCLUDE_FROM_WORLD_pn-prelink_libc-musl = "1"

# error: obstack.h: No such file or directory
EXCLUDE_FROM_WORLD_pn-systemtap_libc-musl = "1"
EXCLUDE_FROM_WORLD_pn-systemtap-uprobes_libc-musl = "1"

# error: a parameter list without types is only allowed in a function definition
#            void (*_function)(sigval_t);
EXCLUDE_FROM_WORLD_pn-qemu_libc-musl = "1"

# error: format '%s' expects argument of type 'char *', but argument 4 has type 'int' [-Werror=format=]
#   snprintf(buf, size, "%s", strerror_r(err, sbuf, sizeof(sbuf)));
EXCLUDE_FROM_WORLD_pn-perf_libc-musl = "1"

# error: 'RTLD_NEXT' was not declared in this scope
EXCLUDE_FROM_WORLD_pn-gcc-sanitizers_libc-musl = "1"
