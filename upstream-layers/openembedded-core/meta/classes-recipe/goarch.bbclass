#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

BUILD_GOOS = "${@go_map_os(d.getVar('BUILD_OS'), d)}"
BUILD_GOARCH = "${@go_map_arch(d.getVar('BUILD_ARCH'), d)}"
BUILD_GOTUPLE = "${BUILD_GOOS}_${BUILD_GOARCH}"
HOST_GOOS = "${@go_map_os(d.getVar('HOST_OS'), d)}"
HOST_GOARCH = "${@go_map_arch(d.getVar('HOST_ARCH'), d)}"
HOST_GOARM = "${@go_map_arm(d.getVar('HOST_ARCH'), d)}"
HOST_GO386 = "${@go_map_386(d.getVar('HOST_ARCH'), d.getVar('TUNE_FEATURES'), d)}"
HOST_GOMIPS = "${@go_map_mips(d.getVar('HOST_ARCH'), d.getVar('TUNE_FEATURES'), d)}"
HOST_GOARM:class-native = "7"
HOST_GO386:class-native = "sse2"
HOST_GOMIPS:class-native = "hardfloat"
HOST_GOTUPLE = "${HOST_GOOS}_${HOST_GOARCH}"
TARGET_GOOS = "${@go_map_os(d.getVar('TARGET_OS'), d)}"
TARGET_GOARCH = "${@go_map_arch(d.getVar('TARGET_ARCH'), d)}"
TARGET_GOARM = "${@go_map_arm(d.getVar('TARGET_ARCH'), d)}"
TARGET_GO386 = "${@go_map_386(d.getVar('TARGET_ARCH'), d.getVar('TUNE_FEATURES'), d)}"
TARGET_GOMIPS = "${@go_map_mips(d.getVar('TARGET_ARCH'), d.getVar('TUNE_FEATURES'), d)}"
TARGET_GOARM:class-native = "7"
TARGET_GO386:class-native = "sse2"
TARGET_GOMIPS:class-native = "hardfloat"
TARGET_GOARM:class-crosssdk = "7"
TARGET_GO386:class-crosssdk = "sse2"
TARGET_GOMIPS:class-crosssdk = "hardfloat"
TARGET_GOTUPLE = "${TARGET_GOOS}_${TARGET_GOARCH}"
GO_BUILD_BINDIR = "${@['bin/${HOST_GOTUPLE}','bin'][d.getVar('BUILD_GOTUPLE') == d.getVar('HOST_GOTUPLE')]}"

# Use the MACHINEOVERRIDES to map ARM CPU architecture passed to GO via GOARM.
# This is combined with *_ARCH to set HOST_GOARM and TARGET_GOARM.
BASE_GOARM = ''
BASE_GOARM:armv7ve = '7'
BASE_GOARM:armv7a = '7'
BASE_GOARM:armv6 = '6'
BASE_GOARM:armv5 = '5'

# Go supports dynamic linking on a limited set of architectures.
# See the supportsDynlink function in go/src/cmd/compile/internal/gc/main.go
GO_DYNLINK = ""
GO_DYNLINK:arm ?= "1"
GO_DYNLINK:aarch64 ?= "1"
GO_DYNLINK:x86 ?= "1"
GO_DYNLINK:x86-64 ?= "1"
GO_DYNLINK:powerpc64 ?= "1"
GO_DYNLINK:powerpc64le ?= "1"
GO_DYNLINK:class-native ?= ""
GO_DYNLINK:class-nativesdk = ""

# define here because everybody inherits this class
#
COMPATIBLE_HOST:linux-gnux32 = "null"
COMPATIBLE_HOST:linux-muslx32 = "null"
COMPATIBLE_HOST:powerpc = "null"
COMPATIBLE_HOST:powerpc64 = "null"
COMPATIBLE_HOST:mipsarchn32 = "null"
COMPATIBLE_HOST:riscv32 = "null"

ARM_INSTRUCTION_SET:armv4 = "arm"
ARM_INSTRUCTION_SET:armv5 = "arm"
ARM_INSTRUCTION_SET:armv6 = "arm"

TUNE_CCARGS:remove = "-march=mips32r2"
SECURITY_NOPIE_CFLAGS ??= ""

# go can't be built with ccache:
# gcc: fatal error: no input files
CCACHE_DISABLE ?= "1"

def go_map_arch(a, d):
    arch = oe.go.map_arch(a)
    if not arch:
        raise bb.parse.SkipRecipe("Unsupported CPU architecture: %s" % a)
    return arch

def go_map_arm(a, d):
    if a.startswith("arm"):
        return d.getVar('BASE_GOARM')
    return ''

def go_map_386(a, f, d):
    import re
    if re.match('i.86', a):
        if ('core2' in f) or ('corei7' in f):
            return 'sse2'
        else:
            return 'softfloat'
    return ''

def go_map_mips(a, f, d):
    import re
    if a == 'mips' or a == 'mipsel':
        if 'fpu-hard' in f:
            return 'hardfloat'
        else:
            return 'softfloat'
    return ''

def go_map_os(o, d):
    if o.startswith('linux'):
        return 'linux'
    return o
