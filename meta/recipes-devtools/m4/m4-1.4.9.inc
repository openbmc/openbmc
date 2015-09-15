require m4.inc

LICENSE = "GPLv2"

LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe\
	file://examples/COPYING;md5=1d49bd61dc590f014cae7173b43e3e5c"

PR = "r2"
SRC_URI += "file://fix_for_circular_dependency.patch"

SRC_URI[md5sum] = "1ba8e147aff5e79bd2bfb983d86b53d5"
SRC_URI[sha256sum] = "815ce53853fbf6493617f467389b799208b1ec98296b95be44a683f8bcfd7c47"

