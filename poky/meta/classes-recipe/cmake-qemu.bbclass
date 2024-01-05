#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#
# Not all platforms are supported by Qemu. Using qemu-user therefore
# involves a certain risk, which is also the reason why this feature
# is not part of the main cmake class by default.
#
# One use case is the execution of cross-compiled unit tests with CTest
# on the build machine. If CMAKE_EXEWRAPPER_ENABLED is configured,
#   cmake --build --target test
# works transparently with qemu-user. If the cmake project is developed
# with this use case in mind this works very nicely also out of an IDE
# configured to use cmake-native for cross compiling.

inherit qemu cmake

DEPENDS:append:class-target = "${@' qemu-native' if bb.utils.contains('MACHINE_FEATURES', 'qemu-usermode', True, False, d) else ''}"

cmake_do_generate_toolchain_file:append:class-target() {
    if [ "${@bb.utils.contains('MACHINE_FEATURES', 'qemu-usermode', 'True', 'False', d)}" ]; then
        # Write out a qemu wrapper that will be used as exe_wrapper so that cmake
        # can run target helper binaries through that. This also allows to execute ctest.
        qemu_binary="${@qemu_wrapper_cmdline(d, '${STAGING_DIR_HOST}', ['${STAGING_DIR_HOST}/${libdir}','${STAGING_DIR_HOST}/${base_libdir}'])}"
        echo "#!/bin/sh" > "${WORKDIR}/cmake-qemuwrapper"
        echo "$qemu_binary \"\$@\"" >> "${WORKDIR}/cmake-qemuwrapper"
        chmod +x "${WORKDIR}/cmake-qemuwrapper"
        echo "set( CMAKE_CROSSCOMPILING_EMULATOR ${WORKDIR}/cmake-qemuwrapper)" \
          >> ${WORKDIR}/toolchain.cmake
    fi
}
