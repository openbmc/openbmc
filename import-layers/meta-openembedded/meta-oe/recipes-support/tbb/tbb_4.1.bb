DESCRIPTION = "Parallelism library for C++ - runtime files \
    TBB is a library that helps you leverage multi-core processor \
    performance without having to be a threading expert. It represents a \
    higher-level, task-based parallelism that abstracts platform details \
    and threading mechanism for performance and scalability."
HOMEPAGE = "http://threadingbuildingblocks.org/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=2c7f2caf277a3933e3acdf7f89d54cc1"
PRDATE = "20130314"
PR = "r${PRDATE}"

SRC_URI = "http://threadingbuildingblocks.org/sites/default/files/software_releases/source/tbb41_${PRDATE}oss_src.tgz \
           file://cross-compile.patch \
           file://allow-to-build-for-older-arm-cores.patch \
           file://tbb.pc"

S = "${WORKDIR}/tbb41_${PRDATE}oss/"

SRC_URI[md5sum] = "ed4af7ccfa122f16cf9920b241633a3a"
SRC_URI[sha256sum] = "32fd5979971b772caa96d40646cee585ed0070516ba2dbbcb1f9b6033d08a92d"

do_compile() {
    oe_runmake compiler=gcc arch=${HOST_ARCH} runtime=cc4
}

do_install() {
    install -d ${D}${includedir} ${D}${libdir}/pkgconfig
    rm ${S}/include/tbb/index.html -f
    cp -R --no-dereference --preserve=mode,links -v ${S}/include/tbb ${D}${includedir}
    install -m 0755 ${B}/build/linux_*_release/lib*.so* ${D}${libdir}
    install -m 0644 ${WORKDIR}/tbb.pc ${D}${libdir}/pkgconfig
}

# fails with thumb enabled:
# | arm-oe-linux-gnueabi-g++  -march=armv7-a -mthumb -mthumb-interwork -mfloat-abi=softfp -mfpu=neon -mtune=cortex-a9 -mcpu=cortex-a9 -D__ARM__ -D__LINUX_ARM_ARCH__=7 -funwind-tables -mvectorize-with-neon-quad -rdynamic --sysroot=/OE/sysroots/m14tv -c -MMD -DTBB_USE_DEBUG  -g -O0 -DUSE_PTHREAD -fPIC -D__TBB_BUILD=1 -Wall -Wno-parentheses -Wno-non-virtual-dtor -O2 -pipe -g -feliminate-unused-debug-types -fpermissive -fvisibility-inlines-hidden   -I../../src -I../../src/rml/include -I../../include ../../src/tbb/concurrent_queue.cpp
# | {standard input}: Assembler messages:
# | {standard input}:250: Error: thumb conditional instruction should be in IT block -- `strexeq r2,r3,[r4]'
# ...
# | make[1]: *** [concurrent_queue.o] Error 1
ARM_INSTRUCTION_SET = "arm"
