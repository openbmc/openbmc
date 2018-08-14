BUILD_GOOS = "${@go_map_os(d.getVar('BUILD_OS'), d)}"
BUILD_GOARCH = "${@go_map_arch(d.getVar('BUILD_ARCH'), d)}"
BUILD_GOTUPLE = "${BUILD_GOOS}_${BUILD_GOARCH}"
HOST_GOOS = "${@go_map_os(d.getVar('HOST_OS'), d)}"
HOST_GOARCH = "${@go_map_arch(d.getVar('HOST_ARCH'), d)}"
HOST_GOARM = "${@go_map_arm(d.getVar('HOST_ARCH'), d.getVar('TUNE_FEATURES'), d)}"
HOST_GO386 = "${@go_map_386(d.getVar('HOST_ARCH'), d.getVar('TUNE_FEATURES'), d)}"
HOST_GOMIPS = "${@go_map_mips(d.getVar('HOST_ARCH'), d.getVar('TUNE_FEATURES'), d)}"
HOST_GOTUPLE = "${HOST_GOOS}_${HOST_GOARCH}"
TARGET_GOOS = "${@go_map_os(d.getVar('TARGET_OS'), d)}"
TARGET_GOARCH = "${@go_map_arch(d.getVar('TARGET_ARCH'), d)}"
TARGET_GOARM = "${@go_map_arm(d.getVar('TARGET_ARCH'), d.getVar('TUNE_FEATURES'), d)}"
TARGET_GO386 = "${@go_map_386(d.getVar('TARGET_ARCH'), d.getVar('TUNE_FEATURES'), d)}"
TARGET_GOMIPS = "${@go_map_mips(d.getVar('TARGET_ARCH'), d.getVar('TUNE_FEATURES'), d)}"
TARGET_GOTUPLE = "${TARGET_GOOS}_${TARGET_GOARCH}"
GO_BUILD_BINDIR = "${@['bin/${HOST_GOTUPLE}','bin'][d.getVar('BUILD_GOTUPLE') == d.getVar('HOST_GOTUPLE')]}"

# Go supports dynamic linking on a limited set of architectures.
# See the supportsDynlink function in go/src/cmd/compile/internal/gc/main.go
GO_DYNLINK = ""
GO_DYNLINK_arm = "1"
GO_DYNLINK_aarch64 = "1"
GO_DYNLINK_x86 = "1"
GO_DYNLINK_x86-64 = "1"
GO_DYNLINK_powerpc64 = "1"
GO_DYNLINK_class-native = ""
GO_DYNLINK_class-nativesdk = ""

# define here because everybody inherits this class
#
COMPATIBLE_HOST_linux-gnux32 = "null"
COMPATIBLE_HOST_linux-muslx32 = "null"
COMPATIBLE_HOST_powerpc = "null"
COMPATIBLE_HOST_powerpc64 = "null"
COMPATIBLE_HOST_mipsarchn32 = "null"
ARM_INSTRUCTION_SET = "arm"
TUNE_CCARGS_remove = "-march=mips32r2"
SECURITY_CFLAGS_mips = "${SECURITY_NOPIE_CFLAGS}"
SECURITY_NOPIE_CFLAGS ??= ""

def go_map_arch(a, d):
    import re
    if re.match('i.86', a):
        return '386'
    elif a == 'x86_64':
        return 'amd64'
    elif re.match('arm.*', a):
        return 'arm'
    elif re.match('aarch64.*', a):
        return 'arm64'
    elif re.match('mips64el.*', a):
        return 'mips64le'
    elif re.match('mips64.*', a):
        return 'mips64'
    elif a == 'mips':
        return 'mips'
    elif a == 'mipsel':
        return 'mipsle'
    elif re.match('p(pc|owerpc)(64)', a):
        return 'ppc64'
    elif re.match('p(pc|owerpc)(64el)', a):
        return 'ppc64le'
    else:
        raise bb.parse.SkipRecipe("Unsupported CPU architecture: %s" % a)

def go_map_arm(a, f, d):
    import re
    if re.match('arm.*', a):
        if 'armv7' in f:
            return '7'
        elif 'armv6' in f:
            return '6'
        elif 'armv5' in f:
            return '5'
    return ''

def go_map_386(a, f, d):
    import re
    if re.match('i.86', a):
        if ('core2' in f) or ('corei7' in f):
            return 'sse2'
        else:
            return '387'
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


