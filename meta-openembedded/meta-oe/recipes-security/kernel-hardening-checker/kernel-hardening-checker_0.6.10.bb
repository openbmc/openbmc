SUMMARY = "A tool for checking the security hardening options of the Linux kernel"
DESCRIPTION = "\
    There are plenty of security hardening options for the Linux kernel; Kconfig \
    options (compile-time); Kernel cmdline arguments (boot-time); Sysctl \
    parameters (runtime). A lot of them have to be enabled manually to make the \
    system more secure which is difficult to track. This tool helps with this \
    task by checking and reporting about the settings compared to a list of \
    recommendation. \
"
HOMEPAGE = "https://github.com/a13xp0p0v/kernel-hardening-checker"
BUGTRACKER = "https://github.com/a13xp0p0v/kernel-hardening-checker/issues"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "git://github.com/a13xp0p0v/kernel-hardening-checker;protocol=https;branch=master"
SRCREV = "f4dbe258ff3d37489962ea9cf210192ae7ff9280"

S = "${UNPACKDIR}/git"

RDEPENDS:${PN} = "\
    python3-json \
"

# /boot/config is required for the analysis
RRECOMMENDS:${PN}:class-target = "\
    kernel-dev \
"

inherit setuptools3

# allow to run on build host, if you don't want it in the image
# oe-run-native kernel-hardening-checker-native kernel-hardening-checker ...
BBCLASSEXTEND = "native"
