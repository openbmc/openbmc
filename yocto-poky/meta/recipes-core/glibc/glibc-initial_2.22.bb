require glibc_${PV}.bb
require glibc-initial.inc

DEPENDS += "kconfig-frontends-native"

# main glibc recipes muck with TARGET_CPPFLAGS to point into
# final target sysroot but we
# are not there when building glibc-initial
# so reset it here

TARGET_CPPFLAGS = ""
