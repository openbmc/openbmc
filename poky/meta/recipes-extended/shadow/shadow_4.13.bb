require shadow.inc

# Build falsely assumes that if --enable-libpam is set, we don't need to link against
# libcrypt. This breaks chsh.
BUILD_LDFLAGS:append:class-target = " ${@bb.utils.contains('DISTRO_FEATURES', 'pam', '-lcrypt', '', d)}"

BBCLASSEXTEND = "native nativesdk"

# Severity is low and marked as closed and won't fix.
# https://bugzilla.redhat.com/show_bug.cgi?id=884658
CVE_CHECK_IGNORE += "CVE-2013-4235"

# This is an issue for a different shadow
CVE_CHECK_IGNORE += "CVE-2016-15024"
