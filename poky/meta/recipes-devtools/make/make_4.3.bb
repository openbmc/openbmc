LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"
require make.inc

SRC_URI += "\
	file://0001-m4-getloadavg.m4-restrict-AIX-specific-test-on-AIX.patch \
	file://0002-modules-fcntl-allow-being-detected-by-importing-proj.patch \
	file://0001-src-dir.c-fix-buffer-overflow-warning.patch \
	file://0002-w32-compat-dirent.c-follow-header.patch \
	file://0003-posixfcn-fcntl-gnulib-make-emulated.patch \
	file://0001-makeinst-Do-not-undef-POSIX-on-clang-arm.patch \
"

EXTRA_OECONF += "--without-guile"

SRC_URI[sha256sum] = "e05fdde47c5f7ca45cb697e973894ff4f5d79e13b750ed57d7b66d8defc78e19"

BBCLASSEXTEND = "native nativesdk"
