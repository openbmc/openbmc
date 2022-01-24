#
# Copyright (C) 2008 OpenedHand Ltd.
#

SUMMARY = "Testing tools/applications"

PR = "r2"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup

# kexec-tools doesn't work on e5500-64b, microblaze and nios2 yet
KEXECTOOLS ?= "kexec"
KEXECTOOLS:e5500-64b ?= ""
KEXECTOOLS:microblaze ?= ""
KEXECTOOLS:nios2 ?= ""
KEXECTOOLS:riscv64 ?= ""
KEXECTOOLS:riscv32 ?= ""

# go does not support ppc32, only ppc64
# https://github.com/golang/go/issues/22885
# gccgo may do better
GOTOOLS ?= "go-helloworld"
GOTOOLS:powerpc ?= ""
GOTOOLS:riscv32 ?= ""

RUSTTOOLS ?= "rust-hello-world"

GSTEXAMPLES ?= "gst-examples"
GSTEXAMPLES:riscv64 = ""

X11GLTOOLS = "\
    mesa-demos \
    "

3GTOOLS = "\
    ofono-tests \
    "

X11TOOLS = "\
    ${GSTEXAMPLES} \
    x11perf \
    xrestop \
    xwininfo \
    xprop \
    "

RDEPENDS:${PN} = "\
    blktool \
    ${KEXECTOOLS} \
    alsa-utils-amixer \
    alsa-utils-aplay \
    ltp \
    connman-tools \
    connman-tests \
    connman-client \
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11', "${X11TOOLS}", "", d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11 opengl', "${X11GLTOOLS}", "", d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', '3g', "${3GTOOLS}", "", d)} \
    ${GOTOOLS} \
    ${RUSTTOOLS} \
    "
