DESCRIPTION = "Parallelism library for C++ - runtime files \
    TBB is a library that helps you leverage multi-core processor \
    performance without having to be a threading expert. It represents a \
    higher-level, task-based parallelism that abstracts platform details \
    and threading mechanism for performance and scalability."
HOMEPAGE = "https://software.intel.com/en-us/tbb"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"
BRANCH = "tbb_2020"
SRCREV = "eca91f16d7490a8abfdee652dadf457ec820cc37"
SRC_URI = "git://github.com/oneapi-src/oneTBB.git;protocol=https;branch=${BRANCH} \
           file://cross-compile.patch \
           file://0001-mallinfo-is-glibc-specific-API-mark-it-so.patch \
           file://GLIBC-PREREQ-is-not-defined-on-musl.patch \
           file://tbb.pc \
           file://improve-reproducibility.patch \
"

S = "${WORKDIR}/git"
PE = "1"

COMPILER ?= "gcc"
COMPILER_toolchain-clang = "clang"

do_compile() {
    oe_runmake compiler=${COMPILER} arch=${HOST_ARCH}
}

do_install() {
    install -d ${D}${includedir} ${D}${libdir}
    rm ${S}/include/tbb/index.html -f
    cp -R --no-dereference --preserve=mode,links -v ${S}/include/tbb ${D}${includedir}
    for f in ${B}/build/linux_*_release/lib*.so*
    do
        install -Dm 0755 $f ${D}${libdir}/
    done
    install -Dm 0644 ${WORKDIR}/tbb.pc ${D}${libdir}/pkgconfig/tbb.pc
}

# fails with thumb enabled:
# | arm-oe-linux-gnueabi-g++  -march=armv7-a -mthumb -mthumb-interwork -mfloat-abi=softfp -mfpu=neon -mtune=cortex-a9 -mcpu=cortex-a9 -D__ARM__ -D__LINUX_ARM_ARCH__=7 -funwind-tables -mvectorize-with-neon-quad -rdynamic --sysroot=/OE/sysroots/m14tv -c -MMD -DTBB_USE_DEBUG  -g -O0 -DUSE_PTHREAD -fPIC -D__TBB_BUILD=1 -Wall -Wno-parentheses -Wno-non-virtual-dtor -O2 -pipe -g -feliminate-unused-debug-types -fpermissive -fvisibility-inlines-hidden   -I../../src -I../../src/rml/include -I../../include ../../src/tbb/concurrent_queue.cpp
# | {standard input}: Assembler messages:
# | {standard input}:250: Error: thumb conditional instruction should be in IT block -- `strexeq r2,r3,[r4]'
# ...
# | make[1]: *** [concurrent_queue.o] Error 1
ARM_INSTRUCTION_SET = "arm"
