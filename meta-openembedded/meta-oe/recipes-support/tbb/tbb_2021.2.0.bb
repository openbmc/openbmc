DESCRIPTION = "Parallelism library for C++ - runtime files \
    TBB is a library that helps you leverage multi-core processor \
    performance without having to be a threading expert. It represents a \
    higher-level, task-based parallelism that abstracts platform details \
    and threading mechanism for performance and scalability."
HOMEPAGE = "https://software.intel.com/en-us/tbb"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=86d3f3a95c324c9479bd8986968f4327"

DEPENDS_append_libc-musl = " libucontext"

PE = "1"

BRANCH = "onetbb_2021"
SRCREV = "2dba2072869a189b9fdab3ffa431d3ea49059a19"
SRC_URI = "git://github.com/oneapi-src/oneTBB.git;protocol=https;branch=${BRANCH} \
            file://0001-mallinfo-is-glibc-specific-API-mark-it-so.patch \
            file://GLIBC-PREREQ-is-not-defined-on-musl.patch \
            file://0001-CMakeLists.txt-exclude-riscv64-riscv32.patch \
            file://0001-Disable-use-of-_tpause-instruction.patch \
            file://0001-set_my_tls_end_of_input-Use-an-arbitrary-but-valid-p.patch \
"

S = "${WORKDIR}/git"

inherit cmake

# test build fails, error: 'mallinfo mallinfo()' is deprecated
EXTRA_OECMAKE += " \
                    -DTBB_TEST=OFF \
                    -DCMAKE_BUILD_TYPE=Release \
                "


# fails with thumb enabled:
# | arm-oe-linux-gnueabi-g++  -march=armv7-a -mthumb -mthumb-interwork -mfloat-abi=softfp -mfpu=neon -mtune=cortex-a9 -mcpu=cortex-a9 -D__ARM__ -D__LINUX_ARM_ARCH__=7 -funwind-tables -mvectorize-with-neon-quad -rdynamic --sysroot=/OE/sysroots/m14tv -c -MMD -DTBB_USE_DEBUG  -g -O0 -DUSE_PTHREAD -fPIC -D__TBB_BUILD=1 -Wall -Wno-parentheses -Wno-non-virtual-dtor -O2 -pipe -g -feliminate-unused-debug-types -fpermissive -fvisibility-inlines-hidden   -I../../src -I../../src/rml/include -I../../include ../../src/tbb/concurrent_queue.cpp
# | {standard input}: Assembler messages:
# | {standard input}:250: Error: thumb conditional instruction should be in IT block -- `strexeq r2,r3,[r4]'
# ...
# | make[1]: *** [concurrent_queue.o] Error 1
ARM_INSTRUCTION_SET = "arm"

ASNEEDED = ""

LDFLAGS_append_mips = " -latomic"
LDFLAGS_append_mipsel = " -latomic"

LDFLAGS_append_libc-musl = " -lucontext"

# The latest version of oneTBB does not support PPC
COMPATIBLE_MACHINE_powerpc = "(!.*ppc).*"
