SUMMARY = "Program to create dependencies in makefiles"

DESCRIPTION = "The gccmakedep program calls 'gcc -M' to output makefile \
rules describing the dependencies of each sourcefile, so that make knows \
which object files must be recompiled when a dependency has changed."

require xorg-util-common.inc
LIC_FILES_CHKSUM = "file://Makefile.am;endline=20;md5=23c277396d690413245ebb89b18c5d4d"
DESCRIPTION = "create dependencies in makefiles using 'gcc -M'"
DEPENDS = "util-macros"
RDEPENDS_${PN} = "gcc"

PR = "r3"
PE = "1"

SRC_URI[md5sum] = "127ddb6131eb4a56fdf6644a63ade788"
SRC_URI[sha256sum] = "f9e2e7a590e27f84b6708ab7a81e546399b949bf652fb9b95193e0e543e6a548"