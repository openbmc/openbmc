DESCRIPTION = "Parallelism library for C++ - runtime files \
    TBB is a library that helps you leverage multi-core processor \
    performance without having to be a threading expert. It represents a \
    higher-level, task-based parallelism that abstracts platform details \
    and threading mechanism for performance and scalability."
HOMEPAGE = "https://software.intel.com/en-us/tbb"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=86d3f3a95c324c9479bd8986968f4327"

DEPENDS:append:libc-musl = " libucontext"
DEPENDS:append:class-target = " hwloc"

PE = "1"

BRANCH = "onetbb_2021"
SRCREV = "1c4c93fc5398c4a1acb3492c02db4699f3048dea"
SRC_URI = "git://github.com/oneapi-src/oneTBB.git;protocol=https;branch=${BRANCH} \
          "

S = "${WORKDIR}/git"

inherit cmake pkgconfig

# test build fails, error: 'mallinfo mallinfo()' is deprecated
EXTRA_OECMAKE += " \
                    -DTBB_TEST=OFF \
                    -DCMAKE_BUILD_TYPE=Release \
                    -DTBB_STRICT=OFF \
                    -DTBB_DISABLE_HWLOC_AUTOMATIC_SEARCH=OFF \
                "

# Hard-float 'd' ABI can't be used for a target that doesn't support the D instruction set extension (ignoring target-abi)
# tmp-glibc/work/riscv64-oe-linux/tbb/1_2021.7.0-r0/recipe-sysroot-native/usr/bin/riscv64-oe-linux/riscv64-oe-linux-ld: /tmp/lto-llvm-264bc2.o: can't link soft-float modules with double-float modules
# tmp-glibc/work/riscv64-oe-linux/tbb/1_2021.7.0-r0/recipe-sysroot-native/usr/bin/riscv64-oe-linux/riscv64-oe-linux-ld: failed to merge target specific data of file /tmp/lto-llvm-264bc2.o
EXTRA_OECMAKE:append:riscv32:toolchain-clang = " -DTBB_ENABLE_IPO=OFF "
EXTRA_OECMAKE:append:riscv64:toolchain-clang = " -DTBB_ENABLE_IPO=OFF "

# fails with thumb enabled:
# | arm-oe-linux-gnueabi-g++  -march=armv7-a -mthumb -mthumb-interwork -mfloat-abi=softfp -mfpu=neon -mtune=cortex-a9 -mcpu=cortex-a9 -D__ARM__ -D__LINUX_ARM_ARCH__=7 -funwind-tables -mvectorize-with-neon-quad -rdynamic --sysroot=/OE/sysroots/m14tv -c -MMD -DTBB_USE_DEBUG  -g -O0 -DUSE_PTHREAD -fPIC -D__TBB_BUILD=1 -Wall -Wno-parentheses -Wno-non-virtual-dtor -O2 -pipe -g -feliminate-unused-debug-types -fpermissive -fvisibility-inlines-hidden   -I../../src -I../../src/rml/include -I../../include ../../src/tbb/concurrent_queue.cpp
# | {standard input}: Assembler messages:
# | {standard input}:250: Error: thumb conditional instruction should be in IT block -- `strexeq r2,r3,[r4]'
# ...
# | make[1]: *** [concurrent_queue.o] Error 1
ARM_INSTRUCTION_SET = "arm"

ASNEEDED = ""

LDFLAGS:append:mips = " -latomic"
LDFLAGS:append:mipsel = " -latomic"
LDFLAGS:append:riscv32:toolchain-clang = " -latomic"

LDFLAGS:append:libc-musl = " -lucontext"

# The latest version of oneTBB does not support PPC
COMPATIBLE_MACHINE:powerpc = "(!.*ppc).*"


do_install:append:class-target() {
    # fix for qa check buildpaths
    sed -i "s#${RECIPE_SYSROOT}##g" ${D}${libdir}/cmake/TBB/TBBTargets.cmake
}


BBCLASSEXTEND = "native nativesdk"
