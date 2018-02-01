# Released under the MIT license (see COPYING.MIT for the terms)

# Applications using this library needs to add link against libbreakpad_client.a.

SUMMARY = "An open-source multi-platform crash reporting system"
DESCRIPTION = "Breakpad is a library and tool suite that allows you to distribute an application to users with compiler-provided debugging information removed, record crashes in compact \"minidump\" files, send them back to your server, and produce C and C++ stack traces from these minidumps. "
HOMEPAGE = "https://code.google.com/p/google-breakpad/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=56c24a43c81c3af6fcf590851931489e"
SECTION = "libs"

inherit autotools

BBCLASSEXTEND = "native"

PE = "1"

PV = "1.0+git${SRCPV}"

SRCREV_FORMAT = "breakpad_glog_gmock_gtest_protobuf_lss"

SRCREV_breakpad = "2f6cb866d615d6240a18c7535c994c6bb93b1ba5"
SRCREV_glog = "d8cb47f77d1c31779f3ff890e1a5748483778d6a"
SRCREV_gmock = "f7d03d2734759ee12b57d2dbcb695607d89e8e05"
SRCREV_gtest = "ec44c6c1675c25b9827aacd08c02433cccde7780"
SRCREV_protobuf = "cb6dd4ef5f82e41e06179dcd57d3b1d9246ad6ac"
SRCREV_lss = "1549d20f6d3e7d66bb4e687c0ab9da42c2bff2ac"

SRC_URI = "git://github.com/google/breakpad;name=breakpad \
           git://github.com/google/glog.git;destsuffix=git/src/third_party/glog;name=glog \
           git://github.com/google/googlemock.git;destsuffix=git/src/testing;name=gmock \
           git://github.com/google/googletest.git;destsuffix=git/src/testing/gtest;name=gtest \
           git://github.com/google/protobuf.git;destsuffix=git/src/third_party/protobuf/protobuf;name=protobuf \
           git://chromium.googlesource.com/linux-syscall-support;protocol=https;destsuffix=git/src/third_party/lss;name=lss \
"
S = "${WORKDIR}/git"

COMPATIBLE_MACHINE_powerpc = "(!.*ppc).*"

do_install_append() {
        install -d ${D}${includedir}
        install -d ${D}${includedir}/breakpad

        install -d ${D}${includedir}/breakpad/client/linux/crash_generation
        install -m 0644 ${S}/src/client/linux/crash_generation/crash_generation_client.h  ${D}${includedir}/breakpad/client/linux/crash_generation/crash_generation_client.h

        install -d ${D}${includedir}/breakpad/client/linux/handler/
        install -m 0644 ${S}/src/client/linux/handler/exception_handler.h ${D}${includedir}/breakpad/client/linux/handler/exception_handler.h
        install -m 0644 ${S}/src/client/linux/handler/minidump_descriptor.h ${D}${includedir}/breakpad/client/linux/handler/minidump_descriptor.h

        install -d ${D}${includedir}/breakpad/client/linux/dump_writer_common
        install -m 0644 ${S}/src/client/linux/dump_writer_common/mapping_info.h ${D}${includedir}/breakpad/client/linux/dump_writer_common/mapping_info.h
        install -m 0644 ${S}/src/client/linux/dump_writer_common/thread_info.h ${D}${includedir}/breakpad/client/linux/dump_writer_common/thread_info.h
        install -m 0644 ${S}/src/client/linux/dump_writer_common/raw_context_cpu.h ${D}${includedir}/breakpad/client/linux/dump_writer_common/raw_context_cpu.h

        install -d ${D}${includedir}/breakpad/client/linux/minidump_writer
        install -m 0644 ${S}/src/client/linux/minidump_writer/linux_dumper.h ${D}${includedir}/breakpad/client/linux/minidump_writer/linux_dumper.h
        install -m 0644 ${S}/src/client/linux/minidump_writer/minidump_writer.h ${D}${includedir}/breakpad/client/linux/minidump_writer/minidump_writer.h

        install -d ${D}${includedir}/breakpad/common
        install -m 0644 ${S}/src/common/memory.h ${D}${includedir}/breakpad/common/memory.h
        install -m 0644 ${S}/src/common/scoped_ptr.h ${D}${includedir}/breakpad/common/scoped_ptr.h
        install -m 0644 ${S}/src/common/using_std_string.h ${D}${includedir}/breakpad/common/using_std_string.h

        install -d ${D}${includedir}/breakpad/google_breakpad/common
        install -m 0644 ${S}/src/google_breakpad/common/breakpad_types.h ${D}${includedir}/breakpad/google_breakpad/common/breakpad_types.h
        install -m 0644 ${S}/src/google_breakpad/common/minidump_format.h ${D}${includedir}/breakpad/google_breakpad/common/minidump_format.h
        install -m 0644 ${S}/src/google_breakpad/common/minidump_cpu_amd64.h ${D}${includedir}/breakpad/google_breakpad/common/minidump_cpu_amd64.h
        install -m 0644 ${S}/src/google_breakpad/common/minidump_cpu_arm.h ${D}${includedir}/breakpad/google_breakpad/common/minidump_cpu_arm.h
        install -m 0644 ${S}/src/google_breakpad/common/minidump_cpu_arm.h ${D}${includedir}/breakpad/google_breakpad/common/minidump_cpu_arm64.h
        install -m 0644 ${S}/src/google_breakpad/common/minidump_cpu_mips.h ${D}${includedir}/breakpad/google_breakpad/common/minidump_cpu_mips.h
        install -m 0644 ${S}/src/google_breakpad/common/minidump_cpu_ppc64.h ${D}${includedir}/breakpad/google_breakpad/common/minidump_cpu_ppc64.h
        install -m 0644 ${S}/src/google_breakpad/common/minidump_cpu_ppc.h ${D}${includedir}/breakpad/google_breakpad/common/minidump_cpu_ppc.h
        install -m 0644 ${S}/src/google_breakpad/common/minidump_cpu_sparc.h ${D}${includedir}/breakpad/google_breakpad/common/minidump_cpu_sparc.h
        install -m 0644 ${S}/src/google_breakpad/common/minidump_cpu_x86.h ${D}${includedir}/breakpad/google_breakpad/common/minidump_cpu_x86.h
        install -m 0644 ${S}/src/google_breakpad/common/minidump_exception_linux.h ${D}${includedir}/breakpad/google_breakpad/common/minidump_exception_linux.h
        install -m 0644 ${S}/src/google_breakpad/common/minidump_exception_mac.h ${D}${includedir}/breakpad/google_breakpad/common/minidump_exception_mac.h
        install -m 0644 ${S}/src/google_breakpad/common/minidump_exception_ps3.h ${D}${includedir}/breakpad/google_breakpad/common/minidump_exception_ps3.h
        install -m 0644 ${S}/src/google_breakpad/common/minidump_exception_solaris.h ${D}${includedir}/breakpad/google_breakpad/common/minidump_exception_solaris.h
        install -m 0644 ${S}/src/google_breakpad/common/minidump_exception_win32.h ${D}${includedir}/breakpad/google_breakpad/common/minidump_exception_win32.h

        install -d ${D}${includedir}/breakpad/third_party/lss
        install -m 0644 ${S}/src/third_party/lss/linux_syscall_support.h ${D}${includedir}/breakpad/third_party/lss/linux_syscall_support.h
}

PACKAGES =+ "${PN}-minidump-upload ${PN}-sym-upload"

FILES_${PN}-minidump-upload = "${bindir}/minidump_upload"
FILES_${PN}-sym-upload = "${bindir}/sym_upload"


SYSROOT_PREPROCESS_FUNCS += "breakpad_populate_sysroot"
breakpad_populate_sysroot() {
        sysroot_stage_dir ${D}/usr/include ${SYSROOT_DESTDIR}/usr/include
        sysroot_stage_dir ${D}/usr/lib ${SYSROOT_DESTDIR}/usr/lib
        sysroot_stage_dir ${D}/usr/lib ${SYSROOT_DESTDIR}/usr/lib
}

# Fails to build with thumb-1 (qemuarm)
#| {standard input}: Assembler messages:
#| {standard input}:2178: Error: selected processor does not support Thumb mode `it ne'
#| {standard input}:2179: Error: Thumb does not support conditional execution
#| {standard input}:2180: Error: selected processor does not support Thumb mode `it eq'
#| {standard input}:2181: Error: Thumb does not support conditional execution
#| {standard input}:2183: Error: lo register required -- `str ip,[r1,#-4]!'
#| {standard input}:2184: Error: Thumb does not support this addressing mode -- `str r6,[r1,#-4]!'
#| {standard input}:2191: Error: lo register required -- `ldr pc,[sp]'
#| make: *** [src/client/linux/handler/exception_handler.o] Error 1
ARM_INSTRUCTION_SET = "arm"
