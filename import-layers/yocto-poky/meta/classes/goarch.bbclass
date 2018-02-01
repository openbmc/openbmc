BUILD_GOOS = "${@go_map_os(d.getVar('BUILD_OS', True), d)}"
BUILD_GOARCH = "${@go_map_arch(d.getVar('BUILD_ARCH', True), d)}"
BUILD_GOTUPLE = "${BUILD_GOOS}_${BUILD_GOARCH}"
HOST_GOOS = "${@go_map_os(d.getVar('HOST_OS', True), d)}"
HOST_GOARCH = "${@go_map_arch(d.getVar('HOST_ARCH', True), d)}"
HOST_GOARM = "${@go_map_arm(d.getVar('HOST_ARCH', True), d.getVar('TUNE_FEATURES', True), d)}"
HOST_GOTUPLE = "${HOST_GOOS}_${HOST_GOARCH}"
TARGET_GOOS = "${@go_map_os(d.getVar('TARGET_OS', True), d)}"
TARGET_GOARCH = "${@go_map_arch(d.getVar('TARGET_ARCH', True), d)}"
TARGET_GOARM = "${@go_map_arm(d.getVar('TARGET_ARCH', True), d.getVar('TUNE_FEATURES', True), d)}"
TARGET_GOTUPLE = "${TARGET_GOOS}_${TARGET_GOARCH}"
GO_BUILD_BINDIR = "${@['bin/${HOST_GOTUPLE}','bin'][d.getVar('BUILD_GOTUPLE',True) == d.getVar('HOST_GOTUPLE',True)]}"

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
    elif re.match('mips64el*', a):
        return 'mips64le'
    elif re.match('mips64*', a):
        return 'mips64'
    elif re.match('mipsel*', a):
        return 'mipsle'
    elif re.match('mips*', a):
        return 'mips'
    elif re.match('p(pc|owerpc)(64)', a):
        return 'ppc64'
    elif re.match('p(pc|owerpc)(64el)', a):
        return 'ppc64le'
    else:
        raise bb.parse.SkipPackage("Unsupported CPU architecture: %s" % a)

def go_map_arm(a, f, d):
    import re
    if re.match('arm.*', a):
        if 'armv7' in f:
            return '7'
        elif 'armv6' in f:
            return '6'
    return ''

def go_map_os(o, d):
    if o.startswith('linux'):
        return 'linux'
    return o


