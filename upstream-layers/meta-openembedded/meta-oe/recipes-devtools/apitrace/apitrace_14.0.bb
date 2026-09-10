SUMMARY = "Tools for tracing OpenGL, Direct3D, and other graphics APIs"
HOMEPAGE = "http://apitrace.github.io/"
SECTION = "console/tools"
LICENSE = "BSD-3-Clause AND MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=aeb969185a143c3c25130bc2c3ef9a50 \
                    file://thirdparty/snappy/COPYING;md5=f62f3080324a97b3159a7a7e61812d0c"

SRCREV = "18cfd388ac47a9e00978b34d6c20341f3e2cdb56"
SRC_URI = "gitsm://github.com/${BPN}/${BPN}.git;branch=master;protocol=https;tag=${PV} \
           file://0001-zstd-disable-the-x86-64-BMI2-assembly-fast-loops.patch \
           "

DEPENDS += "zlib libpng python3-native"

inherit cmake

PACKAGECONFIG ??= " ${@bb.utils.contains('DISTRO_FEATURES', 'x11 opengl', 'x11', '', d)} "
PACKAGECONFIG[x11] = "-DENABLE_X11=ON,-DENABLE_X11=OFF,libx11"

EXTRA_OECMAKE += "\
    -DENABLE_GUI=OFF \
    -DENABLE_STATIC_LIBGCC=OFF \
    -DENABLE_STATIC_LIBSTDCXX=OFF \
    -DPython3_ROOT_DIR=/usr/bin/python3-native \
"

# Use the bundled snappy library
EXTRA_OECMAKE += "\
    -DENABLE_STATIC_SNAPPY=ON \
"
LDFLAGS:append:riscv32 = " -latomic"

SECURITY_CFLAGS:toolchain-clang = ""

# Upstream bakes the absolute python CMake found at configure time
# (Python3_EXECUTABLE) into the apitrace binary as APITRACE_PYTHON_EXECUTABLE.
# That path points into the build host (e.g. the autobuilder's
# /srv/pokybuild/buildbot-venv/bin/python3) which trips the buildpaths QA
# check and does not exist on the target. The diff/leaks helper subcommands
# exec it via os::execute(), which uses execvp(), so resolve "python3" through
# PATH at runtime instead.
do_configure:prepend() {
    sed -i -E 's|(APITRACE_PYTHON_EXECUTABLE=)"[^"]*Python3_EXECUTABLE[^"]*"|\1"python3"|' ${S}/cli/CMakeLists.txt
}
