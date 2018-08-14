require bash.inc

# GPLv2+ (< 4.0), GPLv3+ (>= 4.0)
LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "${GNU_MIRROR}/bash/${BP}.tar.gz;name=tarball \
           file://execute_cmd.patch;striplevel=0 \
           file://mkbuiltins_have_stringize.patch \
           file://build-tests.patch \
           file://test-output.patch \
           file://fix-run-coproc-run-heredoc-run-execscript-run-test-f.patch \
           file://run-ptest \
           file://fix-run-builtins.patch \
           file://0001-help-fix-printf-format-security-warning.patch \
           file://bash-memleak-bug-fix-for-builtin-command-read.patch \
           file://pathexp-dep.patch \
           "

SRC_URI[tarball.md5sum] = "7c112970cbdcadfc331e10eeb5f6aa41"
SRC_URI[tarball.sha256sum] = "57d8432be54541531a496fd4904fdc08c12542f43605a9202594fa5d5f9f2331"


BBCLASSEXTEND = "nativesdk"
