require shadow.inc

# Build falsely assumes that if --enable-libpam is set, we don't need to link against
# libcrypt. This breaks chsh.
BUILD_LDFLAGS:append:class-target = " ${@bb.utils.contains('DISTRO_FEATURES', 'pam', '-lcrypt', '', d)}"

BBCLASSEXTEND = "native nativesdk"

# https://bugzilla.redhat.com/show_bug.cgi?id=884658
CVE_STATUS[CVE-2013-4235] = "upstream-wontfix: Severity is low and marked as closed and won't fix."
CVE_STATUS[CVE-2016-15024] = "cpe-incorrect: This is an issue for a different shadow"
